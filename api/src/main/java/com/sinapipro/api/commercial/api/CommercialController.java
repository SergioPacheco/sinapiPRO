package com.sinapipro.api.commercial.api;

import com.sinapipro.api.commercial.domain.*;
import com.sinapipro.api.shared.api.PageResponse;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Tag(name = "Commercial", description = "Real estate developments, units and sales proposals")
@RestController
@RequestMapping("/api/v1/commercial")
public class CommercialController {

    private final DevelopmentRepository developmentRepository;
    private final DevelopmentUnitRepository unitRepository;
    private final SalesProposalRepository proposalRepository;
    private final BrokerCommissionRepository commissionRepository;

    public CommercialController(DevelopmentRepository developmentRepository, DevelopmentUnitRepository unitRepository,
                                SalesProposalRepository proposalRepository, BrokerCommissionRepository commissionRepository) {
        this.developmentRepository = developmentRepository;
        this.unitRepository = unitRepository;
        this.proposalRepository = proposalRepository;
        this.commissionRepository = commissionRepository;
    }

    // --- Developments ---

    @Operation(summary = "List developments")
    @GetMapping("/developments")
    @PreAuthorize("@perm.check('commercial.read')")
    PageResponse<DevelopmentResponse> listDevelopments(@PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(developmentRepository.findAll(pageable).map(DevelopmentResponse::from));
    }

    @Operation(summary = "Create a development")
    @PostMapping("/developments")
    @PreAuthorize("@perm.check('commercial.write')")
    ResponseEntity<DevelopmentResponse> createDevelopment(@Valid @RequestBody CreateDevelopmentRequest req) {
        var dev = developmentRepository.save(new Development(req.name(), req.address(), req.city(),
                req.state(), req.totalUnits(), req.launchDate()));
        return ResponseEntity.created(URI.create("/api/v1/commercial/developments/" + dev.getId()))
                .body(DevelopmentResponse.from(dev));
    }

    // --- Units ---

    @Operation(summary = "List units of a development (price table)")
    @GetMapping("/developments/{devId}/units")
    @PreAuthorize("@perm.check('commercial.read')")
    List<UnitResponse> listUnits(@PathVariable UUID devId) {
        return unitRepository.findByDevelopmentIdOrderByCode(devId).stream().map(UnitResponse::from).toList();
    }

    @Operation(summary = "List available units")
    @GetMapping("/developments/{devId}/units/available")
    @PreAuthorize("@perm.check('commercial.read')")
    List<UnitResponse> availableUnits(@PathVariable UUID devId) {
        return unitRepository.findByDevelopmentIdAndStatus(devId, "AVAILABLE").stream().map(UnitResponse::from).toList();
    }

    @Operation(summary = "Add a unit to a development")
    @PostMapping("/developments/{devId}/units")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    @ResponseStatus(HttpStatus.CREATED)
    UnitResponse createUnit(@PathVariable UUID devId, @Valid @RequestBody CreateUnitRequest req) {
        var dev = developmentRepository.findById(devId)
                .orElseThrow(() -> new DomainNotFoundException("Development not found: " + devId));
        var unit = unitRepository.save(new DevelopmentUnit(dev, req.code(), req.type(), req.area(),
                req.price(), req.floor(), req.bedrooms()));
        return UnitResponse.from(unit);
    }

    @Operation(summary = "Update unit price")
    @PatchMapping("/units/{unitId}/price")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    UnitResponse updatePrice(@PathVariable UUID unitId, @Valid @RequestBody UpdatePriceRequest req) {
        var unit = unitRepository.findById(unitId)
                .orElseThrow(() -> new DomainNotFoundException("Unit not found: " + unitId));
        unit.setPrice(req.price());
        return UnitResponse.from(unitRepository.save(unit));
    }

    // --- Proposals ---

    @Operation(summary = "List proposals for a development")
    @GetMapping("/developments/{devId}/proposals")
    @PreAuthorize("@perm.check('commercial.read')")
    PageResponse<ProposalResponse> listProposals(@PathVariable UUID devId, @PageableDefault(size = 20) Pageable pageable) {
        return PageResponse.from(proposalRepository.findByUnitDevelopmentId(devId, pageable).map(ProposalResponse::from));
    }

    @Operation(summary = "Create a sales proposal")
    @PostMapping("/proposals")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    ResponseEntity<ProposalResponse> createProposal(@Valid @RequestBody CreateProposalRequest req) {
        var unit = unitRepository.findById(req.unitId())
                .orElseThrow(() -> new DomainNotFoundException("Unit not found: " + req.unitId()));
        var proposal = proposalRepository.save(new SalesProposal(unit, req.clientId(), req.clientName(),
                req.proposalDate(), req.proposedPrice(), req.downPayment(),
                req.installments() != null ? req.installments() : 1, req.notes()));
        return ResponseEntity.created(URI.create("/api/v1/commercial/proposals/" + proposal.getId()))
                .body(ProposalResponse.from(proposal));
    }

    @Operation(summary = "Approve a proposal (reserves the unit)")
    @PostMapping("/proposals/{id}/approve")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    ProposalResponse approveProposal(@PathVariable UUID id) {
        var proposal = findProposal(id);
        proposal.approve();
        return ProposalResponse.from(proposalRepository.save(proposal));
    }

    @Operation(summary = "Sign a proposal (marks unit as sold)")
    @PostMapping("/proposals/{id}/sign")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    ProposalResponse signProposal(@PathVariable UUID id) {
        var proposal = findProposal(id);
        proposal.sign();
        return ProposalResponse.from(proposalRepository.save(proposal));
    }

    @Operation(summary = "Reject a proposal")
    @PostMapping("/proposals/{id}/reject")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    ProposalResponse rejectProposal(@PathVariable UUID id) {
        var proposal = findProposal(id);
        proposal.reject();
        return ProposalResponse.from(proposalRepository.save(proposal));
    }

    @Operation(summary = "Cancel a signed proposal (releases the unit)")
    @PostMapping("/proposals/{id}/cancel")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    ProposalResponse cancelProposal(@PathVariable UUID id) {
        var proposal = findProposal(id);
        proposal.cancel();
        return ProposalResponse.from(proposalRepository.save(proposal));
    }

    private SalesProposal findProposal(UUID id) {
        return proposalRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Proposal not found: " + id));
    }

    // --- Commissions ---

    @Operation(summary = "Add broker commission to a proposal")
    @PostMapping("/proposals/{proposalId}/commissions")
    @PreAuthorize("@perm.check('commercial.write')")
    @ResponseStatus(HttpStatus.CREATED)
    CommissionResponse addCommission(@PathVariable UUID proposalId, @Valid @RequestBody CreateCommissionRequest req) {
        var proposal = findProposal(proposalId);
        var amount = proposal.getProposedPrice().multiply(req.percentage());
        var commission = commissionRepository.save(new BrokerCommission(proposal, req.brokerName(), req.percentage(), amount));
        return CommissionResponse.from(commission);
    }

    @Operation(summary = "List commissions for a proposal")
    @GetMapping("/proposals/{proposalId}/commissions")
    @PreAuthorize("@perm.check('commercial.read')")
    List<CommissionResponse> listCommissions(@PathVariable UUID proposalId) {
        return commissionRepository.findByProposalId(proposalId).stream().map(CommissionResponse::from).toList();
    }

    @Operation(summary = "List pending commissions")
    @GetMapping("/commissions/pending")
    @PreAuthorize("@perm.check('commercial.read')")
    List<CommissionResponse> pendingCommissions() {
        return commissionRepository.findByStatus("PENDING").stream().map(CommissionResponse::from).toList();
    }

    @Operation(summary = "Pay a commission")
    @PostMapping("/commissions/{id}/pay")
    @PreAuthorize("@perm.check('commercial.write')")
    @Transactional
    CommissionResponse payCommission(@PathVariable UUID id, @Valid @RequestBody PayCommissionRequest req) {
        var commission = commissionRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Commission not found: " + id));
        commission.pay(req.paidDate());
        return CommissionResponse.from(commissionRepository.save(commission));
    }

    // --- DTOs ---
    record CreateDevelopmentRequest(@NotBlank String name, String address, String city, String state,
                                    @NotNull Integer totalUnits, LocalDate launchDate) {}
    record CreateUnitRequest(@NotBlank String code, @NotBlank String type, BigDecimal area,
                             @NotNull @Positive BigDecimal price, Integer floor, Integer bedrooms) {}
    record UpdatePriceRequest(@NotNull @Positive BigDecimal price) {}
    record CreateProposalRequest(@NotNull UUID unitId, UUID clientId, @NotBlank String clientName,
                                 @NotNull LocalDate proposalDate, @NotNull @Positive BigDecimal proposedPrice,
                                 BigDecimal downPayment, Integer installments, String notes) {}

    record DevelopmentResponse(UUID id, String name, String address, String city, String state,
                               int totalUnits, String status, LocalDate launchDate) {
        static DevelopmentResponse from(Development d) {
            return new DevelopmentResponse(d.getId(), d.getName(), d.getAddress(), d.getCity(),
                    d.getState(), d.getTotalUnits(), d.getStatus(), d.getLaunchDate());
        }
    }

    record UnitResponse(UUID id, String code, String type, BigDecimal area, BigDecimal price,
                        String status, Integer floor, Integer bedrooms) {
        static UnitResponse from(DevelopmentUnit u) {
            return new UnitResponse(u.getId(), u.getCode(), u.getType(), u.getArea(), u.getPrice(),
                    u.getStatus(), u.getFloor(), u.getBedrooms());
        }
    }

    record ProposalResponse(UUID id, String unitCode, String clientName, LocalDate proposalDate,
                            BigDecimal proposedPrice, BigDecimal downPayment, int installments, String status) {
        static ProposalResponse from(SalesProposal p) {
            return new ProposalResponse(p.getId(), p.getUnit().getCode(), p.getClientName(), p.getProposalDate(),
                    p.getProposedPrice(), p.getDownPayment(), p.getInstallments(), p.getStatus());
        }
    }

    record CreateCommissionRequest(@NotBlank String brokerName, @NotNull @Positive BigDecimal percentage) {}
    record PayCommissionRequest(@NotNull LocalDate paidDate) {}

    record CommissionResponse(UUID id, String brokerName, BigDecimal percentage, BigDecimal amount, String status, LocalDate paidDate) {
        static CommissionResponse from(BrokerCommission c) {
            return new CommissionResponse(c.getId(), c.getBrokerName(), c.getPercentage(), c.getAmount(), c.getStatus(), c.getPaidDate());
        }
    }
}
