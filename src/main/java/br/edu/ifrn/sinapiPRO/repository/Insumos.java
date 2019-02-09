package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.model.InsumoID;
import br.edu.ifrn.sinapiPRO.repository.helper.insumo.InsumosQueries;

@Repository
public interface Insumos extends JpaRepository<Insumo, InsumoID>, InsumosQueries {
	
	public Optional<Insumo> findById(InsumoID insumoID);
	
	List<Insumo> findByInsumoIDBaseInsumo(BaseInsumo baseInsumo);
}
