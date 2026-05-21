package com.sinapipro.api.sinapi.application;

import com.sinapipro.api.shared.error.DomainNotFoundException;
import com.sinapipro.api.sinapi.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class CompositionVersionService {

    private final CompositionRepository compositionRepository;
    private final MaterialRepository materialRepository;

    public CompositionVersionService(CompositionRepository compositionRepository, MaterialRepository materialRepository) {
        this.compositionRepository = compositionRepository;
        this.materialRepository = materialRepository;
    }

    /**
     * Cria nova versão de uma composição própria (copy-on-write).
     * Marca a versão anterior como superseded.
     */
    public Composition updateWithNewVersion(UUID compositionId, String description, String unit, String groupName,
                                            List<ItemInput> items) {
        var current = compositionRepository.findById(compositionId)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + compositionId));
        if (!current.isEditable()) {
            throw new IllegalStateException("Cannot edit SINAPI compositions");
        }

        current.markSuperseded();
        compositionRepository.save(current);

        var newVersion = current.createNewVersion(description, unit, groupName);

        if (items != null) {
            for (var item : items) {
                if (item.itemType() == ItemType.COMPOSITION) {
                    var child = compositionRepository.findById(item.childCompositionId())
                            .orElseThrow(() -> new DomainNotFoundException("Child composition not found: " + item.childCompositionId()));
                    if (child.getId().equals(compositionId)) {
                        throw new IllegalStateException("Circular reference: composition cannot reference itself");
                    }
                    newVersion.addCompositionItem(child, item.coefficient());
                } else {
                    var material = materialRepository.findBySinapiCode(item.materialCode())
                            .orElseThrow(() -> new DomainNotFoundException("Material not found: " + item.materialCode()));
                    newVersion.addItem(material, item.coefficient(), item.itemType());
                }
            }
        }

        return compositionRepository.save(newVersion);
    }

    /**
     * Copia uma composição SINAPI para o catálogo próprio.
     */
    public Composition copyFromSinapi(UUID sinapiCompositionId) {
        var source = compositionRepository.findById(sinapiCompositionId)
                .orElseThrow(() -> new DomainNotFoundException("Composition not found: " + sinapiCompositionId));
        if (!"SINAPI".equals(source.getOrigin())) {
            throw new IllegalStateException("Can only copy SINAPI compositions");
        }

        var copy = new Composition(
                "P-" + source.getSinapiCode(),
                source.getDescription(),
                source.getUnit(),
                source.getGroupName(),
                "PROPRIO"
        );

        for (var item : source.getItems()) {
            if (item.getItemType() == ItemType.COMPOSITION) {
                copy.addCompositionItem(item.getChildComposition(), item.getCoefficient());
            } else {
                copy.addItem(item.getMaterial(), item.getCoefficient(), item.getItemType());
            }
        }

        return compositionRepository.save(copy);
    }

    public record ItemInput(String materialCode, UUID childCompositionId, BigDecimal coefficient, ItemType itemType) {}
}
