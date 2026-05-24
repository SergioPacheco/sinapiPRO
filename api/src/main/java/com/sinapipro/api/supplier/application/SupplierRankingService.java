package com.sinapipro.api.supplier.application;

import com.sinapipro.api.supplier.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class SupplierRankingService {

    private final SupplierEvaluationRepository evaluationRepo;
    private final SupplierRepository supplierRepo;

    public SupplierRankingService(SupplierEvaluationRepository evaluationRepo, SupplierRepository supplierRepo) {
        this.evaluationRepo = evaluationRepo;
        this.supplierRepo = supplierRepo;
    }

    /** 10.1 — Ranking de fornecedores por nota média */
    public List<SupplierRanking> ranking(String category) {
        var suppliers = category != null
                ? supplierRepo.findByCategoryAndActiveTrue(category)
                : supplierRepo.findByActiveTrue();

        return suppliers.stream().map(s -> {
            var avgScore = evaluationRepo.averageScoreBySupplierId(s.getId());
            return new SupplierRanking(s.getId(), s.getName(), s.getCategory(), avgScore, 0);
        }).sorted(Comparator.comparingDouble(SupplierRanking::averageScore).reversed()).toList();
    }

    /** 10.2 — Ranking por categoria */
    public Map<String, List<SupplierRanking>> rankingByCategory() {
        return ranking(null).stream().collect(Collectors.groupingBy(SupplierRanking::category));
    }

    public record SupplierRanking(UUID supplierId, String name, String category, double averageScore, int evaluationCount) {}
}
