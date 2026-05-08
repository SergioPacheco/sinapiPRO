package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.Cidade;
import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.repository.filter.CidadeFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.cidade.CidadesRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;

public interface CidadesRepository extends JpaRepository<Cidade, Long>, CidadesRepositoryQueries,
		FilterableRepository<Cidade, CidadeFilter> {

	public List<Cidade> findByEstadoCodigo(Long codigoEstado);

	public Optional<Cidade> findByNomeAndEstado(String nome, Estado estado);
	
}
