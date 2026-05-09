package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.Cargo;
import com.sinapipro.repository.filter.CargoFilter;
import com.sinapipro.repository.helper.cargo.CargosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface CargosRepository extends JpaRepository<Cargo, Long>, CargosRepositoryQueries,
		NamedEntityRepository<Cargo>, FilterableRepository<Cargo, CargoFilter> {

	Optional<Cargo> findByNomeIgnoreCase(String nome);
}
