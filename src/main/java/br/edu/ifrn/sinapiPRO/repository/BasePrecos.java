package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.helper.baseprecos.BasePrecosQueries;

@Repository
public interface BasePrecos extends JpaRepository<BasePreco, Long>, BasePrecosQueries {

	public Optional<BasePreco> findByNomeIgnoreCase(String nome);
 
}
