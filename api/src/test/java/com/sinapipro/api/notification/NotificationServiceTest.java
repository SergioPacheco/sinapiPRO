package com.sinapipro.api.notification;

import com.sinapipro.api.contract.domain.Contract;
import com.sinapipro.api.contract.domain.ContractRepository;
import com.sinapipro.api.equipment.domain.Equipment;
import com.sinapipro.api.equipment.domain.EquipmentRepository;
import com.sinapipro.api.notification.application.NotificationService;
import com.sinapipro.api.notification.domain.Notification;
import com.sinapipro.api.notification.domain.NotificationRepository;
import com.sinapipro.api.rfi.domain.Rfi;
import com.sinapipro.api.rfi.domain.RfiRepository;
import com.sinapipro.api.rfi.domain.RfiStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private NotificationRepository notificationRepo;
    private RfiRepository rfiRepo;
    private EquipmentRepository equipmentRepo;
    private ContractRepository contractRepo;
    private NotificationService service;

    @BeforeEach
    void setUp() {
        notificationRepo = Mockito.mock(NotificationRepository.class);
        rfiRepo = Mockito.mock(RfiRepository.class);
        equipmentRepo = Mockito.mock(EquipmentRepository.class);
        contractRepo = Mockito.mock(ContractRepository.class);
        service = new NotificationService(notificationRepo, rfiRepo, equipmentRepo, contractRepo);
    }

    @Test
    @DisplayName("should generate RFI overdue alerts")
    void shouldGenerateRfiOverdueAlerts() {
        UUID budgetId = UUID.randomUUID();
        var rfi = Mockito.mock(Rfi.class);
        when(rfi.isOverdue()).thenReturn(true);
        when(rfi.getId()).thenReturn(UUID.randomUUID());
        when(rfi.getNumber()).thenReturn(1);
        when(rfi.getSubject()).thenReturn("Foundation specs");
        when(rfi.getDueDate()).thenReturn(java.time.LocalDate.now().minusDays(5));
        when(rfi.getAssignedTo()).thenReturn("eng@company.com");

        when(rfiRepo.findByBudgetIdAndStatus(budgetId, RfiStatus.OPEN)).thenReturn(List.of(rfi));
        when(notificationRepo.existsByEntityTypeAndEntityIdAndType(eq("RFI"), any(), eq("RFI_OVERDUE"))).thenReturn(false);
        when(equipmentRepo.findMaintenanceDueByHours()).thenReturn(List.of());
        when(equipmentRepo.findMaintenanceDueByDate()).thenReturn(List.of());
        when(contractRepo.findByBudgetIdOrderByNumberAsc(budgetId)).thenReturn(List.of());
        when(notificationRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var alerts = service.generateAlerts(budgetId);

        assertThat(alerts).hasSize(1);
        assertThat(alerts.getFirst().getType()).isEqualTo("RFI_OVERDUE");
        assertThat(alerts.getFirst().getRecipient()).isEqualTo("eng@company.com");
    }

    @Test
    @DisplayName("should not duplicate existing alerts")
    void shouldNotDuplicateAlerts() {
        UUID budgetId = UUID.randomUUID();
        var rfi = Mockito.mock(Rfi.class);
        when(rfi.isOverdue()).thenReturn(true);
        when(rfi.getId()).thenReturn(UUID.randomUUID());

        when(rfiRepo.findByBudgetIdAndStatus(budgetId, RfiStatus.OPEN)).thenReturn(List.of(rfi));
        when(notificationRepo.existsByEntityTypeAndEntityIdAndType(eq("RFI"), any(), eq("RFI_OVERDUE"))).thenReturn(true);
        when(equipmentRepo.findMaintenanceDueByHours()).thenReturn(List.of());
        when(equipmentRepo.findMaintenanceDueByDate()).thenReturn(List.of());
        when(contractRepo.findByBudgetIdOrderByNumberAsc(budgetId)).thenReturn(List.of());
        when(notificationRepo.saveAll(anyList())).thenAnswer(inv -> inv.getArgument(0));

        var alerts = service.generateAlerts(budgetId);

        assertThat(alerts).isEmpty();
    }

    @Test
    @DisplayName("should mark notification as read")
    void shouldMarkRead() {
        UUID id = UUID.randomUUID();
        var notification = new Notification(null, "TEST", "INFO", "Title", "Msg", null, null, "user");
        when(notificationRepo.findById(id)).thenReturn(Optional.of(notification));

        service.markRead(id);

        assertThat(notification.getRead()).isTrue();
    }
}
