package com.courtbooking.CourtBooking.booking.service;

import com.courtbooking.CourtBooking.booking.application.commands.dtos.BookCourtSlot;
import com.courtbooking.CourtBooking.booking.domain.exceptions.CourtNotFoundException;
import com.courtbooking.CourtBooking.booking.domain.exceptions.SlotDefinitionInvalidException;
import com.courtbooking.CourtBooking.facility.repository.ICourtReadRepository;
import com.courtbooking.CourtBooking.pricing.domain.projection.PricingView;
import com.courtbooking.CourtBooking.pricing.domain.projection.SlotDefView;
import com.courtbooking.CourtBooking.pricing.repository.IPricingReadRepository;
import com.courtbooking.CourtBooking.pricing.repository.ISlotDefinitionReadRepository;
import com.courtbooking.CourtBooking.shared.annoation.DomainService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@DomainService
@RequiredArgsConstructor
public class BookingService implements IBookingService {

    //This should be replace by intergration service call in real world application
    private final ISlotDefinitionReadRepository slotRepo;
    private final IPricingReadRepository pricingRepo;
    private final ICourtReadRepository courtRepo;

    @Override
    public BookingComputation computePricingAndValidate(BookCourtSlot cmd) {

        Integer minBookingMinutes = courtRepo.findMinBookingMinutes(cmd.getCourtId());
        if (minBookingMinutes == null) throw new CourtNotFoundException(cmd.getCourtId());

        List<SlotDefView> slots = slotRepo.findActiveSlotsByIds(cmd.getCourtId(), cmd.getSlotDefinitionIds(), cmd.getBookingDate());
        if (slots.size() != cmd.getSlotDefinitionIds().size()) {
            // find missing
            Set<UUID> found = slots.stream().map(SlotDefView::getSlotDefinitionId).collect(Collectors.toSet());
            UUID missing = cmd.getSlotDefinitionIds().stream().filter(id -> !found.contains(id)).findFirst().orElse(null);
            throw new SlotDefinitionInvalidException("SlotDefinition not valid/active/effective for bookingDate. Missing=" + missing);
        }

        // Validate day pattern matches booking date
        DayOfWeek dow = cmd.getBookingDate().getDayOfWeek(); // MON..SUN
        boolean isWeekend = (dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY);

        for (SlotDefView s : slots) {
            if (s.getDayOfWeek() != null) {
                int iso = dow.getValue(); // 1..7
                if (s.getDayOfWeek() != iso) {
                    throw new SlotDefinitionInvalidException("SlotDefinition dayOfWeek mismatch: " + s.getSlotDefinitionId());
                }
            }
            if (s.getDayType() != null) {
                String dt = s.getDayType().toUpperCase();
                if (dt.equals("WEEKDAY") && isWeekend) {
                    throw new SlotDefinitionInvalidException("SlotDefinition dayType WEEKDAY mismatch: " + s.getSlotDefinitionId());
                }
                if (dt.equals("WEEKEND") && !isWeekend) {
                    throw new SlotDefinitionInvalidException("SlotDefinition dayType WEEKEND mismatch: " + s.getSlotDefinitionId());
                }
            }
        }

        // Compute per-item final price (snapshot)
        List<ItemPrice> itemPrices = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        String currency = (cmd.getCurrency() == null || cmd.getCurrency().isBlank())
                ? "INR"
                : cmd.getCurrency().trim().toUpperCase();

        for (SlotDefView s : slots) {
            PricingView pricing = pricingRepo.findPricing(s.getPricePlanId());
            if (pricing == null) {
                throw new SlotDefinitionInvalidException("PricingPlan not found/active for slotDefinitionId=" + s.getSlotDefinitionId());
            }

            // If pricing currency differs from request, choose your policy:
            // Here: enforce request currency equals plan currency (simple)
            if (!pricing.getCurrency().equalsIgnoreCase(currency)) {
                throw new SlotDefinitionInvalidException("Currency mismatch. pricing=" + pricing.getCurrency() + " request=" + currency);
            }

            long minutes = Duration.between(s.getStartTime(), s.getEndTime()).toMinutes();
            if (minutes <= 0 || minutes % minBookingMinutes != 0) {
                throw new SlotDefinitionInvalidException("Slot duration not aligned with minBookingMinutes. slotDefinitionId=" + s.getSlotDefinitionId());
            }

            long units = minutes / minBookingMinutes;
            BigDecimal finalPrice = pricing.getPricePerUnit().multiply(BigDecimal.valueOf(units));

            total = total.add(finalPrice);
            itemPrices.add(new ItemPrice(s.getSlotDefinitionId(), s.getPricePlanId(), finalPrice));
        }

        return new BookingComputation(currency, total, itemPrices, minBookingMinutes);
    }

    @Data
    @AllArgsConstructor
    public static class BookingComputation {
        private String currency;
        private BigDecimal totalAmount;
        private List<ItemPrice> items;
        private Integer minBookingMinutes;
    }

    @Data
    @AllArgsConstructor
    public static class ItemPrice {
        private UUID slotDefinitionId;
        private UUID pricePlanId;   // snapshot id
        private BigDecimal finalPrice;
    }
}
