package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.Cargo;
import br.edu.ifrn.sinapiPRO.repository.filter.CargoFilter;
import br.edu.ifrn.sinapiPRO.repository.helper.cargo.CargosRepositoryQueries;
import br.edu.ifrn.sinapiPRO.repository.support.FilterableRepository;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface CargosRepository extends JpaRepository<Cargo, Long>, CargosRepositoryQueries,
		NamedEntityRepository<Cargo>, FilterableRepository<Cargo, CargoFilter> {

	Optional<Cargo> findByNomeIgnoreCase(String nome);
}
