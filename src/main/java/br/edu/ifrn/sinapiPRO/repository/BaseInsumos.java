package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.helper.baseinsumos.BaseInsumosQueries;

@Repository
public interface BaseInsumos extends JpaRepository<BaseInsumo, Long>, BaseInsumosQueries {

	public Optional<BaseInsumo> findByNomeIgnoreCase(String nome);

}
