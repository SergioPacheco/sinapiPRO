package com.sinapipro.api.finance.application;

import com.sinapipro.api.shared.domain.TenantAwareEntity;
import jakarta.persistence.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

// --- Entities ---

@Entity @Table(name = "tax_config")
class TaxConfig extends TenantAwareEntity {
    @Column(name = "tax_type", nullable = false, length = 20) private String taxType;
    @Column(length = 2) private String state;
    @Column(length = 100) private String city;
    @Column(nullable = false, precision = 8, scale = 4) private BigDecimal rate;
    @Column(name = "minimum_amount", precision = 18, scale = 2) private BigDecimal minimumAmount;
    @Column(nullable = false) private boolean active = true;
    protected TaxConfig() {}
    public TaxConfig(String taxType, String state, String city, BigDecimal rate) { this.taxType = taxType; this.state = state; this.city = city; this.rate = rate; }
    public String getTaxType() { return taxType; } public String getState() { return state; }
    public BigDecimal getRate() { return rate; } public boolean isActive() { return active; }
}

@Entity @Table(name = "tax_guide")
class TaxGuide extends TenantAwareEntity {
    @Column(name = "tax_type", nullable = false, length = 20) private String taxType;
    @Column(name = "reference_month", nullable = false) private LocalDate referenceMonth;
    @Column(nullable = false, precision = 18, scale = 2) private BigDecimal amount;
    @Column(name = "due_date", nullable = false) private LocalDate dueDate;
    @Column(nullable = false) private boolean paid;
    @Column(name = "paid_date") private LocalDate paidDate;
    @Column(name = "guide_number", length = 30) private String guideNumber;
    protected TaxGuide() {}
    public TaxGuide(String taxType, LocalDate referenceMonth, BigDecimal amount, LocalDate dueDate) { this.taxType = taxType; this.referenceMonth = referenceMonth; this.amount = amount; this.dueDate = dueDate; }
    public String getTaxType() { return taxType; } public BigDecimal getAmount() { return amount; }
    public LocalDate getDueDate() { return dueDate; } public boolean isPaid() { return paid; }
    public void markPaid(LocalDate date, String guideNumber) { this.paid = true; this.paidDate = date; this.guideNumber = guideNumber; }
}

@Entity @Table(name = "invoice_book")
class InvoiceBookEntry extends TenantAwareEntity {
    @Column(name = "invoice_number", nullable = false, length = 30) private String invoiceNumber;
    @Column(nullable = false, length = 10) private String type; // ENTRADA, SAIDA
    @Column(name = "issue_date", nullable = false) private LocalDate issueDate;
    @Column(name = "supplier_id") private UUID supplierId;
    @Column(name = "client_id") private UUID clientId;
    @Column(length = 300) private String description;
    @Column(name = "gross_amount", nullable = false, precision = 18, scale = 2) private BigDecimal grossAmount;
    @Column(name = "net_amount", nullable = false, precision = 18, scale = 2) private BigDecimal netAmount;
    @Column(name = "iss_amount", precision = 18, scale = 2) private BigDecimal issAmount;
    @Column(name = "inss_amount", precision = 18, scale = 2) private BigDecimal inssAmount;
    @Column(name = "ir_amount", precision = 18, scale = 2) private BigDecimal irAmount;
    @Column(name = "project_id") private UUID projectId;
    protected InvoiceBookEntry() {}
    public InvoiceBookEntry(String invoiceNumber, String type, LocalDate issueDate, BigDecimal grossAmount, BigDecimal netAmount) { this.invoiceNumber = invoiceNumber; this.type = type; this.issueDate = issueDate; this.grossAmount = grossAmount; this.netAmount = netAmount; }
    public String getInvoiceNumber() { return invoiceNumber; } public String getType() { return type; }
    public LocalDate getIssueDate() { return issueDate; } public BigDecimal getGrossAmount() { return grossAmount; }
    public BigDecimal getNetAmount() { return netAmount; }
    public void setSupplier(UUID id) { this.supplierId = id; } public void setClient(UUID id) { this.clientId = id; }
    public void setProject(UUID id) { this.projectId = id; }
    public void setRetentions(BigDecimal iss, BigDecimal inss, BigDecimal ir) { this.issAmount = iss; this.inssAmount = inss; this.irAmount = ir; }
}

// --- Repositories ---
interface TaxConfigRepository extends JpaRepository<TaxConfig, UUID> { List<TaxConfig> findByTaxTypeAndActiveTrue(String taxType); }
interface TaxGuideRepository extends JpaRepository<TaxGuide, UUID> { List<TaxGuide> findByReferenceMonthAndPaidFalse(LocalDate month); }
interface InvoiceBookRepository extends JpaRepository<InvoiceBookEntry, UUID> { List<InvoiceBookEntry> findByIssueDateBetweenOrderByIssueDate(LocalDate from, LocalDate to); }

// --- Service ---
@Service @Transactional
public class FiscalService {
    private final TaxConfigRepository configRepo;
    private final TaxGuideRepository guideRepo;
    private final InvoiceBookRepository bookRepo;

    public FiscalService(TaxConfigRepository configRepo, TaxGuideRepository guideRepo, InvoiceBookRepository bookRepo) {
        this.configRepo = configRepo; this.guideRepo = guideRepo; this.bookRepo = bookRepo;
    }

    /** 11.1 — Cadastro de impostos com alíquotas */
    public TaxConfig createTaxConfig(String taxType, String state, String city, BigDecimal rate) {
        return configRepo.save(new TaxConfig(taxType, state, city, rate));
    }

    /** 11.3 — Gerar guia de recolhimento */
    public TaxGuide generateGuide(String taxType, LocalDate referenceMonth, BigDecimal amount, LocalDate dueDate) {
        return guideRepo.save(new TaxGuide(taxType, referenceMonth, amount, dueDate));
    }

    /** 11.3 — Pagar guia */
    public TaxGuide payGuide(UUID guideId, LocalDate paidDate, String guideNumber) {
        var guide = guideRepo.findById(guideId).orElseThrow();
        guide.markPaid(paidDate, guideNumber);
        return guideRepo.save(guide);
    }

    /** 11.3 — Guias pendentes */
    public List<TaxGuide> pendingGuides(LocalDate month) { return guideRepo.findByReferenceMonthAndPaidFalse(month); }

    /** 11.4 — Livro de NF (entrada/saída) */
    public InvoiceBookEntry registerInvoice(String number, String type, LocalDate issueDate, BigDecimal gross, BigDecimal net) {
        return bookRepo.save(new InvoiceBookEntry(number, type, issueDate, gross, net));
    }

    /** 11.4 — Consultar livro por período */
    public List<InvoiceBookEntry> invoiceBook(LocalDate from, LocalDate to) { return bookRepo.findByIssueDateBetweenOrderByIssueDate(from, to); }

    /** 11.5 — Integração NFS-e (emissão via prefeitura) */
    public NfseResult emitNfse(NfseRequest request) {
        // Integration point: each municipality has its own webservice
        // Production: use specific adapter per city (ABRASF standard)
        return new NfseResult(request.invoiceNumber(), "PENDING", null,
                "NFS-e queued for emission. Municipality: " + request.cityCode());
    }

    /** 11.5 — Consultar status NFS-e */
    public NfseResult queryNfse(String protocol) {
        // Production: query municipality webservice by protocol
        return new NfseResult(null, "UNKNOWN", protocol, "Query not implemented for this municipality");
    }

    /** 11.5 — Cancelar NFS-e */
    public NfseResult cancelNfse(String invoiceNumber, String reason) {
        return new NfseResult(invoiceNumber, "CANCEL_REQUESTED", null, "Cancellation requested: " + reason);
    }

    public record NfseRequest(String invoiceNumber, String cityCode, String serviceCode,
                               BigDecimal amount, String description, UUID clientId) {}
    public record NfseResult(String invoiceNumber, String status, String protocol, String message) {}
}
