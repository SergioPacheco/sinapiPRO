package com.sinapipro.api.contract.application;

import com.sinapipro.api.budget.domain.Budget;
import com.sinapipro.api.budget.domain.BudgetRepository;
import com.sinapipro.api.contract.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.supplier.domain.Supplier;
import com.sinapipro.api.supplier.domain.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class ContractService {

    private final ContractRepository contractRepository;
    private final BudgetRepository budgetRepository;
    private final SupplierRepository supplierRepository;

    public ContractService(ContractRepository contractRepository, BudgetRepository budgetRepository,
                           SupplierRepository supplierRepository) {
        this.contractRepository = contractRepository;
        this.budgetRepository = budgetRepository;
        this.supplierRepository = supplierRepository;
    }

    @Transactional
    public Contract create(UUID budgetId, UUID supplierId, String number, String description,
                           BigDecimal originalValue, BigDecimal retentionPct, LocalDate startDate, LocalDate endDate) {
        Budget budget = budgetRepository.findById(budgetId)
                .orElseThrow(() -> new DomainNotFoundException("Budget not found: " + budgetId));
        Supplier supplier = supplierRepository.findById(supplierId)
                .orElseThrow(() -> new DomainNotFoundException("Supplier not found: " + supplierId));
        Contract contract = new Contract(budget, supplier, number, description, originalValue, retentionPct, startDate, endDate);
        return contractRepository.save(contract);
    }

    @Transactional
    public Contract activate(UUID contractId) {
        Contract contract = findOrThrow(contractId);
        contract.activate();
        return contractRepository.save(contract);
    }

    @Transactional
    public ChangeOrder addChangeOrder(UUID contractId, Integer number, String description,
                                      BigDecimal amount, String justification) {
        Contract contract = findOrThrow(contractId);
        if (contract.getStatus() != ContractStatus.ACTIVE) {
            throw new IllegalStateException("Can only add change orders to ACTIVE contracts");
        }
        ChangeOrder co = new ChangeOrder(contract, number, description, amount, justification);
        contract.getChangeOrders().add(co);
        contractRepository.save(contract);
        return co;
    }

    @Transactional
    public ChangeOrder approveChangeOrder(UUID contractId, UUID changeOrderId) {
        Contract contract = findOrThrow(contractId);
        ChangeOrder co = contract.getChangeOrders().stream()
                .filter(c -> c.getId().equals(changeOrderId)).findFirst()
                .orElseThrow(() -> new DomainNotFoundException("Change order not found: " + changeOrderId));
        co.approve();
        contractRepository.save(contract);
        return co;
    }

    @Transactional
    public ChangeOrder rejectChangeOrder(UUID contractId, UUID changeOrderId) {
        Contract contract = findOrThrow(contractId);
        ChangeOrder co = contract.getChangeOrders().stream()
                .filter(c -> c.getId().equals(changeOrderId)).findFirst()
                .orElseThrow(() -> new DomainNotFoundException("Change order not found: " + changeOrderId));
        co.reject();
        contractRepository.save(contract);
        return co;
    }

    public ContractFinancialSummary financialSummary(UUID contractId) {
        Contract contract = findOrThrow(contractId);
        BigDecimal updatedValue = contract.getUpdatedValue();
        BigDecimal retentionAmount = updatedValue.multiply(contract.getRetentionPct()).setScale(2, RoundingMode.HALF_UP);
        BigDecimal netPayable = updatedValue.subtract(retentionAmount);
        long approvedChangeOrders = contract.getChangeOrders().stream()
                .filter(co -> co.getStatus() == ChangeOrderStatus.APPROVED).count();
        BigDecimal changeOrderTotal = contract.getChangeOrders().stream()
                .filter(co -> co.getStatus() == ChangeOrderStatus.APPROVED)
                .map(ChangeOrder::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return new ContractFinancialSummary(contract.getOriginalValue(), changeOrderTotal,
                updatedValue, contract.getRetentionPct(), retentionAmount, netPayable, approvedChangeOrders);
    }

    public List<Contract> listByBudget(UUID budgetId) {
        return contractRepository.findByBudgetIdOrderByNumberAsc(budgetId);
    }

    private Contract findOrThrow(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Contract not found: " + id));
    }

    public record ContractFinancialSummary(BigDecimal originalValue, BigDecimal changeOrderTotal,
                                           BigDecimal updatedValue, BigDecimal retentionPct,
                                           BigDecimal retentionAmount, BigDecimal netPayable,
                                           long approvedChangeOrders) {}
}
