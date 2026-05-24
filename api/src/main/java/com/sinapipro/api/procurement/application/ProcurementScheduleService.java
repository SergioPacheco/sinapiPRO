package com.sinapipro.api.procurement.application;

import com.sinapipro.api.procurement.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ProcurementScheduleService {

    private final ProcurementScheduleRepository scheduleRepository;

    public ProcurementScheduleService(ProcurementScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public ProcurementSchedule create(UUID projectId, String materialDescription,
                                       LocalDate plannedDate, BigDecimal quantity, BigDecimal estimatedCost) {
        return scheduleRepository.save(new ProcurementSchedule(projectId, materialDescription, plannedDate, quantity, estimatedCost));
    }

    public List<ProcurementSchedule> listByProject(UUID projectId) {
        return scheduleRepository.findByProjectIdOrderByPlannedDate(projectId);
    }

    public List<ProcurementSchedule> listPlanned(UUID projectId) {
        return scheduleRepository.findByProjectIdAndStatusOrderByPlannedDate(projectId, "PLANNED");
    }

    public ProcurementSchedule linkToOrder(UUID scheduleId, UUID purchaseOrderId) {
        var schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new DomainNotFoundException("Schedule item not found: " + scheduleId));
        schedule.linkOrder(purchaseOrderId);
        return scheduleRepository.save(schedule);
    }

    public ProcurementSchedule markReceived(UUID scheduleId) {
        var schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new DomainNotFoundException("Schedule item not found: " + scheduleId));
        schedule.markReceived();
        return scheduleRepository.save(schedule);
    }
}
