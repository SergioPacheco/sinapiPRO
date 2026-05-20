package com.sinapipro.api.dailylog.api;

import com.sinapipro.api.dailylog.application.DailyLogReportService;
import com.sinapipro.api.dailylog.application.DailyLogService;
import com.sinapipro.api.dailylog.application.DailyLogService.*;
import com.sinapipro.api.dailylog.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Daily Log", description = "Daily construction log (diário de obra)")
@RestController
@RequestMapping("/api/v1/projects/{projectId}/daily-logs")
public class DailyLogController {

    private final DailyLogRepository dailyLogRepository;
    private final DailyLogService dailyLogService;
    private final DailyLogReportService dailyLogReportService;

    public DailyLogController(DailyLogRepository dailyLogRepository, DailyLogService dailyLogService,
                              DailyLogReportService dailyLogReportService) {
        this.dailyLogRepository = dailyLogRepository;
        this.dailyLogService = dailyLogService;
        this.dailyLogReportService = dailyLogReportService;
    }

    @Operation(summary = "List daily logs for a budget")
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    PageResponse<DailyLogResponse> list(@PathVariable UUID projectId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(dailyLogRepository.findByBudgetId(projectId, pageable).map(DailyLogResponse::from));
    }

    @Operation(summary = "Get daily log detail with all entries")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    DailyLogDetailResponse get(@PathVariable UUID projectId, @PathVariable UUID id) {
        return DailyLogDetailResponse.from(findInProject(projectId, id));
    }

    @Operation(summary = "Create a daily log entry")
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    ResponseEntity<DailyLogResponse> create(@PathVariable UUID projectId, @Valid @RequestBody CreateDailyLogRequest req) {
        List<LaborInput> labor = req.labor() != null
                ? req.labor().stream().map(l -> new LaborInput(l.workerName(), l.role(), l.hours())).toList() : null;
        List<EquipmentInput> equipment = req.equipment() != null
                ? req.equipment().stream().map(e -> new EquipmentInput(e.equipmentName(), e.hoursUsed(), e.hoursIdle())).toList() : null;
        List<OccurrenceInput> occurrences = req.occurrences() != null
                ? req.occurrences().stream().map(o -> new OccurrenceInput(o.type(), o.description())).toList() : null;

        DailyLog saved = dailyLogService.create(projectId, req.logDate(), req.weatherMorning(),
                req.weatherAfternoon(), req.observations(), labor, equipment, occurrences);
        return ResponseEntity.created(URI.create("/api/v1/projects/" + projectId + "/daily-logs/" + saved.getId()))
                .body(DailyLogResponse.from(saved));
    }

    @Operation(summary = "Summary of all daily logs (total hours, occurrences)")
    @GetMapping("/summary")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    DailyLogSummary summary(@PathVariable UUID projectId) {
        return dailyLogService.summary(projectId);
    }

    // --- Add entries to existing log ---

    @Operation(summary = "Add labor entry to an existing daily log")
    @PostMapping("/{id}/labor")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    LaborResponse addLabor(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody LaborEntry req) {
        var log = findInProject(projectId, id);
        var entry = new DailyLogLabor(log, req.workerName(), req.role(), req.hours());
        log.getLaborEntries().add(entry);
        dailyLogRepository.save(log);
        return new LaborResponse(entry.getId(), entry.getWorkerName(), entry.getRole(), entry.getHours());
    }

    @Operation(summary = "Add equipment entry to an existing daily log")
    @PostMapping("/{id}/equipment")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    EquipmentResponse addEquipment(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody EquipmentEntry req) {
        var log = findInProject(projectId, id);
        var entry = new DailyLogEquipment(log, req.equipmentName(), req.hoursUsed(),
                req.hoursIdle() != null ? req.hoursIdle() : BigDecimal.ZERO);
        log.getEquipmentEntries().add(entry);
        dailyLogRepository.save(log);
        return new EquipmentResponse(entry.getId(), entry.getEquipmentName(), entry.getHoursUsed(), entry.getHoursIdle());
    }

    @Operation(summary = "Add occurrence to an existing daily log")
    @PostMapping("/{id}/occurrences")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    OccurrenceResponse addOccurrence(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody OccurrenceEntry req) {
        var log = findInProject(projectId, id);
        var entry = new DailyLogOccurrence(log, req.type(), req.description());
        log.getOccurrences().add(entry);
        dailyLogRepository.save(log);
        return new OccurrenceResponse(entry.getId(), entry.getType(), entry.getDescription());
    }

    @Operation(summary = "Add photo reference to an existing daily log")
    @PostMapping("/{id}/photos")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    PhotoResponse addPhoto(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody PhotoEntry req) {
        var log = findInProject(projectId, id);
        var entry = new DailyLogPhoto(log, req.filePath(), req.caption());
        log.getPhotos().add(entry);
        dailyLogRepository.save(log);
        return new PhotoResponse(entry.getId(), entry.getFilePath(), entry.getCaption());
    }

    @Operation(summary = "RDO PDF report (daily construction log)")
    @GetMapping(value = "/{id}/reports/rdo.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> rdoReport(@PathVariable UUID projectId, @PathVariable UUID id) {
        findInProject(projectId, id);
        byte[] pdf = dailyLogReportService.generateRdoPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=rdo-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    // --- Tasks (4.5: vinculação ao planejamento) ---

    @Operation(summary = "Add task entry to an existing daily log (links to schedule activity)")
    @PostMapping("/{id}/tasks")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    TaskResponse addTask(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody TaskEntry req) {
        var log = findInProject(projectId, id);
        var entry = new DailyLogTask(log, req.activityId(), req.description(), req.progressPct());
        log.getTasks().add(entry);
        dailyLogRepository.save(log);
        return new TaskResponse(entry.getId(), entry.getActivityId(), entry.getDescription(), entry.getProgressPct());
    }

    // --- Materials (4.6: entrada/saída de materiais) ---

    @Operation(summary = "Add material entry/exit to an existing daily log")
    @PostMapping("/{id}/materials")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    MaterialResponse addMaterial(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody MaterialEntry req) {
        var log = findInProject(projectId, id);
        var entry = new DailyLogMaterial();
        entry.setDailyLog(log);
        entry.setType(req.type());
        entry.setDescription(req.description());
        entry.setQuantity(req.quantity());
        entry.setUnit(req.unit());
        entry.setInvoiceNumber(req.invoiceNumber());
        entry.setNotes(req.notes());
        log.getMaterials().add(entry);
        dailyLogRepository.save(log);
        return new MaterialResponse(entry.getId(), entry.getType(), entry.getDescription(),
                entry.getQuantity(), entry.getUnit(), entry.getInvoiceNumber());
    }

    // --- Signature (6.7: assinatura digital do fiscal) ---

    @Operation(summary = "Sign a daily log (digital signature by inspector)")
    @PostMapping("/{id}/sign")
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.write')")
    @Transactional
    SignatureResponse sign(@PathVariable UUID projectId, @PathVariable UUID id, @Valid @RequestBody SignRequest req) {
        var log = findInProject(projectId, id);
        log.sign(req.signedBy(), req.signatureHash());
        dailyLogRepository.save(log);
        return new SignatureResponse(log.getSignedBy(), log.getSignedAt(), log.getSignatureHash());
    }

    // --- Photo report (4.7: relatório fotográfico PDF) ---

    @Operation(summary = "Photo report PDF (photos with captions)")
    @GetMapping(value = "/{id}/reports/photo-report.pdf", produces = MediaType.APPLICATION_PDF_VALUE)
    @PreAuthorize("hasAuthority('SCOPE_sinapipro.read')")
    ResponseEntity<byte[]> photoReport(@PathVariable UUID projectId, @PathVariable UUID id) {
        findInProject(projectId, id);
        byte[] pdf = dailyLogReportService.generatePhotoReportPdf(id);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=photo-report-" + id + ".pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }

    private DailyLog findInProject(UUID projectId, UUID id) {
        DailyLog log = dailyLogRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Daily log not found: " + id));
        if (!projectId.equals(log.getBudget().getId())) {
            throw new DomainNotFoundException("Daily log not found in project: " + id);
        }
        return log;
    }

    // --- DTOs ---
    record CreateDailyLogRequest(@NotNull LocalDate logDate, String weatherMorning, String weatherAfternoon,
                                 String observations, List<LaborEntry> labor, List<EquipmentEntry> equipment,
                                 List<OccurrenceEntry> occurrences) {}
    record LaborEntry(@NotBlank String workerName, @NotBlank String role, @NotNull BigDecimal hours) {}
    record EquipmentEntry(@NotBlank String equipmentName, @NotNull BigDecimal hoursUsed, BigDecimal hoursIdle) {}
    record OccurrenceEntry(@NotBlank String type, @NotBlank String description) {}
    record PhotoEntry(@NotBlank String filePath, String caption) {}
    record TaskEntry(UUID activityId, @NotBlank String description, BigDecimal progressPct) {}
    record MaterialEntry(@NotBlank String type, @NotBlank String description, @NotNull BigDecimal quantity,
                         String unit, String invoiceNumber, String notes) {}
    record SignRequest(@NotBlank String signedBy, @NotBlank String signatureHash) {}

    record DailyLogResponse(UUID id, LocalDate logDate, String weatherMorning, String weatherAfternoon,
                            String observations, int laborCount, int equipmentCount, int occurrenceCount,
                            int photoCount, int taskCount, int materialCount, String signedBy) {
        static DailyLogResponse from(DailyLog d) {
            return new DailyLogResponse(d.getId(), d.getLogDate(), d.getWeatherMorning(), d.getWeatherAfternoon(),
                    d.getObservations(), d.getLaborEntries().size(), d.getEquipmentEntries().size(),
                    d.getOccurrences().size(), d.getPhotos().size(), d.getTasks().size(),
                    d.getMaterials().size(), d.getSignedBy());
        }
    }

    record DailyLogDetailResponse(UUID id, LocalDate logDate, String weatherMorning, String weatherAfternoon,
                                  String observations, List<LaborResponse> labor, List<EquipmentResponse> equipment,
                                  List<OccurrenceResponse> occurrences, List<PhotoResponse> photos,
                                  List<TaskResponse> tasks, List<MaterialResponse> materials,
                                  String signedBy, java.time.Instant signedAt) {
        static DailyLogDetailResponse from(DailyLog d) {
            return new DailyLogDetailResponse(d.getId(), d.getLogDate(), d.getWeatherMorning(), d.getWeatherAfternoon(),
                    d.getObservations(),
                    d.getLaborEntries().stream().map(l -> new LaborResponse(l.getId(), l.getWorkerName(), l.getRole(), l.getHours())).toList(),
                    d.getEquipmentEntries().stream().map(e -> new EquipmentResponse(e.getId(), e.getEquipmentName(), e.getHoursUsed(), e.getHoursIdle())).toList(),
                    d.getOccurrences().stream().map(o -> new OccurrenceResponse(o.getId(), o.getType(), o.getDescription())).toList(),
                    d.getPhotos().stream().map(p -> new PhotoResponse(p.getId(), p.getFilePath(), p.getCaption())).toList(),
                    d.getTasks().stream().map(t -> new TaskResponse(t.getId(), t.getActivityId(), t.getDescription(), t.getProgressPct())).toList(),
                    d.getMaterials().stream().map(m -> new MaterialResponse(m.getId(), m.getType(), m.getDescription(), m.getQuantity(), m.getUnit(), m.getInvoiceNumber())).toList(),
                    d.getSignedBy(), d.getSignedAt());
        }
    }

    record LaborResponse(UUID id, String workerName, String role, BigDecimal hours) {}
    record EquipmentResponse(UUID id, String equipmentName, BigDecimal hoursUsed, BigDecimal hoursIdle) {}
    record OccurrenceResponse(UUID id, String type, String description) {}
    record PhotoResponse(UUID id, String filePath, String caption) {}
    record TaskResponse(UUID id, UUID activityId, String description, BigDecimal progressPct) {}
    record MaterialResponse(UUID id, String type, String description, BigDecimal quantity, String unit, String invoiceNumber) {}
    record SignatureResponse(String signedBy, java.time.Instant signedAt, String signatureHash) {}
}
