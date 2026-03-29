package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.TipoUnidade;
import br.edu.ifrn.sinapiPRO.repository.helper.tipounidade.TipoUnidadesRepositoryQueries;

@Repository
public interface TipoUnidadesRepository extends JpaRepository<TipoUnidade, Long>, TipoUnidadesRepositoryQueries {

	Optional<TipoUnidade> findByNomeIgnoreCase(String nome);
}
