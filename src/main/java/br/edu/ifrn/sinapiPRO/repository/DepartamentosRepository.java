package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Departamento;
import br.edu.ifrn.sinapiPRO.repository.filter.DepartamentoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.departamento.DepartamentosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface DepartamentosRepository extends JpaRepository<Departamento, Long>, DepartamentosRepositoryQueries,
		NamedEntityRepository<Departamento>, FilterableRepository<Departamento, DepartamentoFilter> {

	Optional<Departamento> findByNomeIgnoreCase(String nome);
}
