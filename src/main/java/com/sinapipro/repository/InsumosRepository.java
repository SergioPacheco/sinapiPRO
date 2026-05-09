package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.BaseInsumo;
import com.sinapipro.model.Insumo;
import com.sinapipro.repository.helper.insumo.InsumosRepositoryQueries;

@Repository
public interface InsumosRepository extends JpaRepository<Insumo, Long>, InsumosRepositoryQueries {
	
	public Optional<Insumo> findByBaseInsumoAndCodigoInsumo(BaseInsumo baseInsumo, String codigoInsumo);
	
	public Optional<Insumo> findById(Long codigo);

	Long countByBaseInsumoCodigo(Long codigo);
	
}
