package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.helper.especieinsumo.EspecieInsumosRepositoryQueries;

@Repository
public interface EspecieInsumosRepository extends JpaRepository<EspecieInsumo, Long>, EspecieInsumosRepositoryQueries {

	Optional<EspecieInsumo> findByNomeIgnoreCase(String nome);
}
