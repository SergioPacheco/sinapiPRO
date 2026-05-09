package com.sinapipro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.PlanejamentoItem;

@Repository
public interface PlanejamentoItemRepository extends JpaRepository<PlanejamentoItem, Long> {

	List<PlanejamentoItem> findByOrcamentoCodigo(Long codigoOrcamento);

	void deleteByOrcamentoCodigo(Long codigoOrcamento);
}
