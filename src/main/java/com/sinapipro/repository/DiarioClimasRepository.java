package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.DiarioClima;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface DiarioClimasRepository extends JpaRepository<DiarioClima, Long>, NamedEntityRepository<DiarioClima> {

	Optional<DiarioClima> findByNomeIgnoreCase(String nome);
}
