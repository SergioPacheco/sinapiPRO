package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Empresa;
import br.edu.ifrn.sinapiPRO.repository.filter.EmpresaFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.empresa.EmpresasRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface EmpresasRepository extends JpaRepository<Empresa, Long>, EmpresasRepositoryQueries,
		NamedEntityRepository<Empresa>, FilterableRepository<Empresa, EmpresaFilter> {

	Optional<Empresa> findByNomeIgnoreCase(String nome);
}
