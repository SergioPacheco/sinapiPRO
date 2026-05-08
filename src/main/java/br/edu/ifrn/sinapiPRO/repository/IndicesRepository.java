package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Indice;
import br.edu.ifrn.sinapiPRO.repository.filter.IndiceFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.indice.IndicesRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface IndicesRepository extends JpaRepository<Indice, Long>, IndicesRepositoryQueries,
		NamedEntityRepository<Indice>, FilterableRepository<Indice, IndiceFilter> {

	Optional<Indice> findByNomeIgnoreCase(String nome);
}
