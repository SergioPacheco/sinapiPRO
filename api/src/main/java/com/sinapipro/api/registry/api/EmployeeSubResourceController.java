package com.sinapipro.api.registry.api;

import com.sinapipro.api.registry.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.domain.MedicalExamResult;
import com.sinapipro.api.shared.domain.MedicalExamType;
import com.sinapipro.api.shared.domain.RegulatoryStandard;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.util.UUID;

@Tag(name = "Employee Sub-Resources", description = "Trainings, EPI deliveries and medical exams per employee")
@RestController
@RequestMapping("/api/v1/registry/employees/{employeeId}")
public class EmployeeSubResourceController {

    private final EmployeeRepository employeeRepository;
    private final EmployeeTrainingRepository trainingRepository;
    private final EmployeeEpiDeliveryRepository epiRepository;
    private final EmployeeMedicalExamRepository medicalExamRepository;

    public EmployeeSubResourceController(EmployeeRepository employeeRepository,
                                         EmployeeTrainingRepository trainingRepository,
                                         EmployeeEpiDeliveryRepository epiRepository,
                                         EmployeeMedicalExamRepository medicalExamRepository) {
        this.employeeRepository = employeeRepository;
        this.trainingRepository = trainingRepository;
        this.epiRepository = epiRepository;
        this.medicalExamRepository = medicalExamRepository;
    }

    // --- Trainings ---

    @Operation(summary = "List trainings for an employee")
    @GetMapping("/trainings")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<EmployeeTraining> listTrainings(@PathVariable UUID employeeId, @PageableDefault(size = 20) Pageable pageable) {
        ensureEmployeeExists(employeeId);
        return PageResponse.from(trainingRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId, pageable));
    }

    @Operation(summary = "Add training to employee")
    @PostMapping("/trainings")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<EmployeeTraining> createTraining(@PathVariable UUID employeeId, @Valid @RequestBody CreateTrainingRequest req) {
        ensureEmployeeExists(employeeId);
        var training = trainingRepository.save(new EmployeeTraining(employeeId, req.trainingName(), req.regulatoryStandard(), req.completionDate(), req.expiryDate(), req.hours(), req.institution(), req.certificatePath(), req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/registry/employees/" + employeeId + "/trainings/" + training.getId())).body(training);
    }

    @Operation(summary = "Update a training")
    @PutMapping("/trainings/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    EmployeeTraining updateTraining(@PathVariable UUID employeeId, @PathVariable UUID id, @Valid @RequestBody CreateTrainingRequest req) {
        ensureEmployeeExists(employeeId);
        var training = trainingRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Training not found: " + id));
        training.update(req.trainingName(), req.regulatoryStandard(), req.completionDate(), req.expiryDate(), req.hours(), req.institution(), req.certificatePath(), req.notes());
        return trainingRepository.save(training);
    }

    @Operation(summary = "Delete a training")
    @DeleteMapping("/trainings/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteTraining(@PathVariable UUID employeeId, @PathVariable UUID id) {
        ensureEmployeeExists(employeeId);
        if (!trainingRepository.existsById(id)) throw new DomainNotFoundException("Training not found: " + id);
        trainingRepository.deleteById(id);
    }

    // --- EPI Deliveries ---

    @Operation(summary = "List EPI deliveries for an employee")
    @GetMapping("/epi-deliveries")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<EmployeeEpiDelivery> listEpiDeliveries(@PathVariable UUID employeeId, @PageableDefault(size = 20) Pageable pageable) {
        ensureEmployeeExists(employeeId);
        return PageResponse.from(epiRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId, pageable));
    }

    @Operation(summary = "Add EPI delivery to employee")
    @PostMapping("/epi-deliveries")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<EmployeeEpiDelivery> createEpiDelivery(@PathVariable UUID employeeId, @Valid @RequestBody CreateEpiDeliveryRequest req) {
        ensureEmployeeExists(employeeId);
        var epi = epiRepository.save(new EmployeeEpiDelivery(employeeId, req.epiDescription(), req.caNumber(), req.deliveryDate(), req.expiryDate(), req.quantity(), req.signaturePath()));
        return ResponseEntity.created(URI.create("/api/v1/registry/employees/" + employeeId + "/epi-deliveries/" + epi.getId())).body(epi);
    }

    @Operation(summary = "Update an EPI delivery")
    @PutMapping("/epi-deliveries/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    EmployeeEpiDelivery updateEpiDelivery(@PathVariable UUID employeeId, @PathVariable UUID id, @Valid @RequestBody CreateEpiDeliveryRequest req) {
        ensureEmployeeExists(employeeId);
        var epi = epiRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("EPI delivery not found: " + id));
        epi.update(req.epiDescription(), req.caNumber(), req.deliveryDate(), req.expiryDate(), req.quantity(), req.signaturePath());
        return epiRepository.save(epi);
    }

    @Operation(summary = "Delete an EPI delivery")
    @DeleteMapping("/epi-deliveries/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteEpiDelivery(@PathVariable UUID employeeId, @PathVariable UUID id) {
        ensureEmployeeExists(employeeId);
        if (!epiRepository.existsById(id)) throw new DomainNotFoundException("EPI delivery not found: " + id);
        epiRepository.deleteById(id);
    }

    // --- Medical Exams ---

    @Operation(summary = "List medical exams for an employee")
    @GetMapping("/medical-exams")
    @PreAuthorize("@perm.check('registry.read')")
    PageResponse<EmployeeMedicalExam> listMedicalExams(@PathVariable UUID employeeId, @PageableDefault(size = 20) Pageable pageable) {
        ensureEmployeeExists(employeeId);
        return PageResponse.from(medicalExamRepository.findByEmployeeIdOrderByCreatedAtDesc(employeeId, pageable));
    }

    @Operation(summary = "Add medical exam to employee")
    @PostMapping("/medical-exams")
    @PreAuthorize("@perm.check('registry.write')")
    ResponseEntity<EmployeeMedicalExam> createMedicalExam(@PathVariable UUID employeeId, @Valid @RequestBody CreateMedicalExamRequest req) {
        ensureEmployeeExists(employeeId);
        var exam = medicalExamRepository.save(new EmployeeMedicalExam(employeeId, req.examType(), req.examDate(), req.expiryDate(), req.physician(), req.crm(), req.result(), req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/registry/employees/" + employeeId + "/medical-exams/" + exam.getId())).body(exam);
    }

    @Operation(summary = "Update a medical exam")
    @PutMapping("/medical-exams/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    EmployeeMedicalExam updateMedicalExam(@PathVariable UUID employeeId, @PathVariable UUID id, @Valid @RequestBody CreateMedicalExamRequest req) {
        ensureEmployeeExists(employeeId);
        var exam = medicalExamRepository.findById(id).orElseThrow(() -> new DomainNotFoundException("Medical exam not found: " + id));
        exam.update(req.examType(), req.examDate(), req.expiryDate(), req.physician(), req.crm(), req.result(), req.notes());
        return medicalExamRepository.save(exam);
    }

    @Operation(summary = "Delete a medical exam")
    @DeleteMapping("/medical-exams/{id}")
    @PreAuthorize("@perm.check('registry.write')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    void deleteMedicalExam(@PathVariable UUID employeeId, @PathVariable UUID id) {
        ensureEmployeeExists(employeeId);
        if (!medicalExamRepository.existsById(id)) throw new DomainNotFoundException("Medical exam not found: " + id);
        medicalExamRepository.deleteById(id);
    }

    // --- Helpers ---

    private void ensureEmployeeExists(UUID employeeId) {
        if (!employeeRepository.existsById(employeeId)) throw new DomainNotFoundException("Employee not found: " + employeeId);
    }

    // --- DTOs ---

    record CreateTrainingRequest(@NotBlank String trainingName, RegulatoryStandard regulatoryStandard,
                                 @NotNull LocalDate completionDate, LocalDate expiryDate, Integer hours,
                                 String institution, String certificatePath, String notes) {}

    record CreateEpiDeliveryRequest(@NotBlank String epiDescription, String caNumber,
                                    @NotNull LocalDate deliveryDate, LocalDate expiryDate,
                                    int quantity, String signaturePath) {}

    record CreateMedicalExamRequest(@NotNull MedicalExamType examType, @NotNull LocalDate examDate,
                                    LocalDate expiryDate, String physician, String crm,
                                    @NotNull MedicalExamResult result, String notes) {}
}
