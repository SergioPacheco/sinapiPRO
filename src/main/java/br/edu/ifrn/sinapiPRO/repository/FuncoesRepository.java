package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Funcao;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncaoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.funcao.FuncoesRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface FuncoesRepository extends JpaRepository<Funcao, Long>, FuncoesRepositoryQueries,
		NamedEntityRepository<Funcao>, FilterableRepository<Funcao, FuncaoFilter> {

	Optional<Funcao> findByNomeIgnoreCase(String nome);
}
