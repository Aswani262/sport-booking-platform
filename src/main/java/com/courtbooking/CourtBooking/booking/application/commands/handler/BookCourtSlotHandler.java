package com.courtbooking.CourtBooking.booking.application.commands.handler;

import com.courtbooking.CourtBooking.booking.application.commands.dtos.BookCourtSlot;
import com.courtbooking.CourtBooking.booking.domain.exceptions.SlotAlreadyBookedException;
import com.courtbooking.CourtBooking.booking.domain.model.Booking;
import com.courtbooking.CourtBooking.booking.domain.model.BookingItem;
import com.courtbooking.CourtBooking.booking.domain.model.BookingType;
import com.courtbooking.CourtBooking.booking.repository.IBookingItemRepository;
import com.courtbooking.CourtBooking.booking.repository.IBookingRepository;
import com.courtbooking.CourtBooking.booking.service.BookCourtSlotValidator;
import com.courtbooking.CourtBooking.booking.service.IBookingService;
import com.courtbooking.CourtBooking.shared.annoation.ApplicationService;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import com.courtbooking.CourtBooking.shared.exception.StructuralValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@ApplicationService
@RequiredArgsConstructor
public class BookCourtSlotHandler implements BookCourtSlotUseCase {

    private final BookCourtSlotValidator validator;
    private final IBookingService bookingService;
    private final IBookingRepository bookingRepository;
    private final IBookingItemRepository bookingItemRepository;

    @Override
    @Transactional
    public UUID handle(BookCourtSlot cmd) {

        // 1) structural validation
        Notification n = validator.validate(cmd);
        if (n.hasErrors()) throw new StructuralValidationException(n);

        // 2) business validation + pricing computation
        var pricing = bookingService.computePricingAndValidate(cmd);

        // 3) create booking (hold)
        int holdMinutes = (cmd.getHoldMinutes() == null) ? 10 : cmd.getHoldMinutes();
        OffsetDateTime nowUtc = OffsetDateTime.now(ZoneOffset.UTC);

        UUID bookingId = UUID.randomUUID();

        Booking booking = Booking.builder()
                .bookingId(bookingId)
                .userId(cmd.getUserId())
                .facilityId(cmd.getFacilityId())
                .courtId(cmd.getCourtId())
                .bookingDate(nowUtc)
                .status(Booking.BookingStatus.PAYMENT_PENDING)
                .holdExpiresAt(nowUtc.plusMinutes(holdMinutes))
                .bookingType(BookingType.ONE_TIME) // if you have enum in your project
                .totalAmount(pricing.getTotalAmount())
                .currency(pricing.getCurrency())
                .paymentTransactionId(null)
                .build();

        bookingRepository.save(booking);

        // 4) insert items one-by-one so we can pinpoint which slot conflicts
        for (var item : pricing.getItems()) {
            BookingItem bookingItem = BookingItem.builder()
                    .bookingItemId(UUID.randomUUID())
                    .bookingId(bookingId)
                    .courtId(cmd.getCourtId())
                    .slotDefinitionId(item.getSlotDefinitionId())
                    .bookingDate(cmd.getBookingDate())
                    .status(BookingItem.BookingItemStatus.HELD)
                    .priceRuleId(item.getPricePlanId())
                    .finalPrice(item.getFinalPrice())
                    .createdAt(nowUtc)
                    .build();

            try {
                bookingItemRepository.insertHeldItem(bookingItem);
            } catch (DuplicateKeyException ex) {
                // Unique index violation => someone else already HELD/CONFIRMED this slot for that date
                throw new SlotAlreadyBookedException(item.getSlotDefinitionId(), cmd.getBookingDate());
            }
        }

        // If any item insert fails, transaction rolls back booking + previously inserted items.
        return bookingId;
    }
}
