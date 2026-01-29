package com.courtbooking.CourtBooking.pricing.application.commands.handler;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;
import com.courtbooking.CourtBooking.pricing.domain.model.PlanType;
import com.courtbooking.CourtBooking.pricing.domain.model.PricePlanPriority;
import com.courtbooking.CourtBooking.pricing.domain.model.PricingPlan;
import com.courtbooking.CourtBooking.pricing.repository.IPricingPlanRepository;
import com.courtbooking.CourtBooking.pricing.service.AddPricingPlanValidator;
import com.courtbooking.CourtBooking.pricing.service.IPricingPlanService;
import com.courtbooking.CourtBooking.shared.annoation.ApplicationService;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import com.courtbooking.CourtBooking.shared.exception.StructuralValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationService
@RequiredArgsConstructor
public class AddPricingPlanHandler implements AddPricingPlanUseCase {

    private final AddPricingPlanValidator validator;
    private final IPricingPlanService pricingPlanService;
    private final IPricingPlanRepository pricingPlanRepository;

    @Override
    @Transactional
    public UUID handle(AddPricingPlan cmd) {

        // 1) structural validation (notification)
        Notification n = validator.validate(cmd);
        if (n.hasErrors()) throw new StructuralValidationException(n);

        // 2) business validation (throws BusinessValidationException subclasses)
        pricingPlanService.validateBusinessRules(cmd);

        // 3) create immutable plan
        PricingPlan plan = PricingPlan.builder()
                .pricingPlanId(UUID.randomUUID())
                .facilityId(cmd.getFacilityId())
                .courtId(cmd.getCourtId())
                .name(cmd.getName().trim())
                .currency(cmd.getCurrency().trim().toUpperCase())
                .active(true)
                .validFrom(cmd.getValidFrom())
                .validTo(cmd.getValidTo())
                .dayOfWeek(cmd.getDayOfWeek())
                .startTime(cmd.getStartTime())
                .endTime(cmd.getEndTime())
                .pricePerUnit(cmd.getPricePerUnit())
                .planType(PlanType.valueOf(cmd.getPlanType().trim().toUpperCase()))
                .priority(PricePlanPriority.valueOf(cmd.getPriority()))
                .build();

        pricingPlanRepository.save(plan);

        return plan.getPricingPlanId();
    }
}
