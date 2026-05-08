package com.sinapipro.api.notification.application;

import com.sinapipro.api.contract.domain.Contract;
import com.sinapipro.api.contract.domain.ContractRepository;
import com.sinapipro.api.contract.domain.ContractStatus;
import com.sinapipro.api.equipment.domain.Equipment;
import com.sinapipro.api.equipment.domain.EquipmentRepository;
import com.sinapipro.api.notification.domain.Notification;
import com.sinapipro.api.notification.domain.NotificationRepository;
import com.sinapipro.api.rfi.domain.Rfi;
import com.sinapipro.api.rfi.domain.RfiRepository;
import com.sinapipro.api.rfi.domain.RfiStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RfiRepository rfiRepository;
    private final EquipmentRepository equipmentRepository;
    private final ContractRepository contractRepository;

    public NotificationService(NotificationRepository notificationRepository, RfiRepository rfiRepository,
                               EquipmentRepository equipmentRepository, ContractRepository contractRepository) {
        this.notificationRepository = notificationRepository;
        this.rfiRepository = rfiRepository;
        this.equipmentRepository = equipmentRepository;
        this.contractRepository = contractRepository;
    }

    /**
     * Generate alerts by scanning cross-module data for actionable conditions.
     */
    @Transactional
    public List<Notification> generateAlerts(UUID budgetId) {
        List<Notification> alerts = new ArrayList<>();

        // RFI overdue alerts
        List<Rfi> openRfis = rfiRepository.findByBudgetIdAndStatus(budgetId, RfiStatus.OPEN);
        for (Rfi rfi : openRfis) {
            if (rfi.isOverdue() && !notificationRepository.existsByEntityTypeAndEntityIdAndType("RFI", rfi.getId(), "RFI_OVERDUE")) {
                alerts.add(new Notification(budgetId, "RFI_OVERDUE", "WARNING",
                        "RFI #" + rfi.getNumber() + " is overdue",
                        "RFI '" + rfi.getSubject() + "' was due on " + rfi.getDueDate(),
                        "RFI", rfi.getId(), rfi.getAssignedTo()));
            }
        }

        // Equipment maintenance due
        for (Equipment eq : equipmentRepository.findMaintenanceDueByHours()) {
            alerts.add(new Notification(null, "MAINTENANCE_DUE", "WARNING",
                    "Equipment " + eq.getCode() + " needs maintenance",
                    eq.getName() + " has exceeded maintenance hours limit",
                    "EQUIPMENT", eq.getId(), null));
        }
        for (Equipment eq : equipmentRepository.findMaintenanceDueByDate()) {
            alerts.add(new Notification(null, "MAINTENANCE_DUE", "WARNING",
                    "Equipment " + eq.getCode() + " maintenance overdue",
                    eq.getName() + " maintenance was due on " + eq.getNextMaintenanceDate(),
                    "EQUIPMENT", eq.getId(), null));
        }

        // Contracts expiring within 30 days
        List<Contract> contracts = contractRepository.findByBudgetIdOrderByNumberAsc(budgetId);
        for (Contract c : contracts) {
            if (c.getStatus() == ContractStatus.ACTIVE && c.getEndDate() != null
                    && c.getEndDate().isBefore(LocalDate.now().plusDays(30))) {
                alerts.add(new Notification(budgetId, "CONTRACT_EXPIRING", "INFO",
                        "Contract " + c.getNumber() + " expiring soon",
                        "Contract '" + c.getDescription() + "' ends on " + c.getEndDate(),
                        "CONTRACT", c.getId(), null));
            }
        }

        notificationRepository.saveAll(alerts);
        return alerts;
    }

    public Page<Notification> getUnread(String recipient, Pageable pageable) {
        return notificationRepository.findByRecipientAndReadFalseOrderByCreatedAtDesc(recipient, pageable);
    }

    @Transactional
    public void markRead(UUID notificationId) {
        notificationRepository.findById(notificationId).ifPresent(Notification::markRead);
    }

    public long countUnread(String recipient) {
        return notificationRepository.countByRecipientAndReadFalse(recipient);
    }
}
