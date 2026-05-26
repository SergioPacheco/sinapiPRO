package com.sinapipro.api.registry.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface InputCategoryRepository extends JpaRepository<InputCategory, UUID> {
    List<InputCategory> findByParentIdIsNullAndActiveTrue();
    List<InputCategory> findByParentIdAndActiveTrue(UUID parentId);
}
