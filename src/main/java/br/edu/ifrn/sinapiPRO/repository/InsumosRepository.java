package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.helper.insumo.InsumosRepositoryQueries;

@Repository
public interface InsumosRepository extends JpaRepository<Insumo, Long>, InsumosRepositoryQueries {
	
	public Optional<Insumo> findByBaseInsumoAndCodigoInsumo(BaseInsumo baseInsumo, String codigoInsumo);
	
	public Optional<Insumo> findById(Long codigo);

	Long countByBaseInsumoCodigo(Long codigo);
	
}
