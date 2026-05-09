package com.sinapipro.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.ComposicaoItem;

@Repository
public interface ComposicaoItemRepository extends JpaRepository<ComposicaoItem, Long> {
	
	
}
