package com.sinapipro.api.dailylog.api;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.dailylog.application.DailyLogReportService;
import com.sinapipro.api.dailylog.application.DailyLogService;
import com.sinapipro.api.dailylog.domain.DailyLog;
import com.sinapipro.api.dailylog.domain.DailyLogRepository;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyLogControllerTest {

    @Mock DailyLogRepository dailyLogRepository;
    @Mock DailyLogService dailyLogService;
    @Mock DailyLogReportService dailyLogReportService;

    private DailyLogController controller;

    @BeforeEach
    void setUp() {
        controller = new DailyLogController(dailyLogRepository, dailyLogService, dailyLogReportService);
    }

    @Test
    @DisplayName("should reject detail access when daily log belongs to another project")
    void shouldRejectDetailWhenLogBelongsToAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();

        DailyLog log = createLog(anotherProjectId);
        when(dailyLogRepository.findById(logId)).thenReturn(Optional.of(log));

        assertThatThrownBy(() -> controller.get(projectId, logId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Daily log not found in project");
    }

    @Test
    @DisplayName("should reject photo creation when daily log belongs to another project")
    void shouldRejectPhotoWhenLogBelongsToAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();

        DailyLog log = createLog(anotherProjectId);
        when(dailyLogRepository.findById(logId)).thenReturn(Optional.of(log));

        assertThatThrownBy(() -> controller.addPhoto(projectId, logId,
                new DailyLogController.PhotoEntry("/tmp/photo.jpg", "Frente da obra")))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Daily log not found in project");

        verify(dailyLogRepository, never()).save(any());
    }

    @Test
    @DisplayName("should reject report access when daily log belongs to another project")
    void shouldRejectReportWhenLogBelongsToAnotherProject() {
        UUID projectId = UUID.randomUUID();
        UUID anotherProjectId = UUID.randomUUID();
        UUID logId = UUID.randomUUID();

        DailyLog log = createLog(anotherProjectId);
        when(dailyLogRepository.findById(logId)).thenReturn(Optional.of(log));

        assertThatThrownBy(() -> controller.rdoReport(projectId, logId))
                .isInstanceOf(DomainNotFoundException.class)
                .hasMessageContaining("Daily log not found in project");

        verify(dailyLogReportService, never()).generateRdoPdf(any());
    }

    private DailyLog createLog(UUID projectId) {
        Budget budget = mock(Budget.class);
        when(budget.getId()).thenReturn(projectId);
        DailyLog log = new DailyLog(budget, LocalDate.now(), "SUNNY", "CLOUDY", "Observacoes");
        log.getLaborEntries().add(new com.sinapipro.api.dailylog.domain.DailyLogLabor(log, "Operario", "Pedreiro", new BigDecimal("8")));
        return log;
    }
}
