package com.sinapipro.api.commercial.application;

import com.sinapipro.api.commercial.domain.*;
import com.sinapipro.api.shared.error.DomainNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Service
@Transactional
public class SaleContractService {

    private final SaleContractRepository contractRepository;

    public SaleContractService(SaleContractRepository contractRepository) {
        this.contractRepository = contractRepository;
    }

    public SaleContract create(UUID developmentId, String contractNumber, LocalDate contractDate,
                                BigDecimal totalAmount, int installmentCount, String amortizationType,
                                BigDecimal downPayment, UUID indexId, BigDecimal interestRate) {
        var contract = new SaleContract(developmentId, contractNumber, contractDate, totalAmount, installmentCount, amortizationType);
        if (downPayment != null) contract.setDownPayment(downPayment);
        contract.setFinancedAmount(totalAmount.subtract(downPayment != null ? downPayment : BigDecimal.ZERO));
        if (indexId != null) contract.setIndexId(indexId);
        if (interestRate != null) contract.setInterestRate(interestRate);
        return contractRepository.save(contract);
    }

    public Page<SaleContract> listByDevelopment(UUID developmentId, Pageable pageable) {
        return contractRepository.findByDevelopmentId(developmentId, pageable);
    }

    public SaleContract findById(UUID id) {
        return contractRepository.findById(id)
                .orElseThrow(() -> new DomainNotFoundException("Sale contract not found: " + id));
    }

    public SaleContract sign(UUID contractId, LocalDate signingDate) {
        var contract = findById(contractId);
        contract.sign(signingDate != null ? signingDate : LocalDate.now());
        return contractRepository.save(contract);
    }

    public SaleContract activate(UUID contractId) {
        var contract = findById(contractId);
        contract.activate();
        return contractRepository.save(contract);
    }

    public SaleContract complete(UUID contractId) {
        var contract = findById(contractId);
        contract.complete();
        return contractRepository.save(contract);
    }

    public SaleContract setBroker(UUID contractId, UUID brokerId, BigDecimal commissionRate) {
        var contract = findById(contractId);
        contract.setBroker(brokerId, commissionRate);
        return contractRepository.save(contract);
    }
}
