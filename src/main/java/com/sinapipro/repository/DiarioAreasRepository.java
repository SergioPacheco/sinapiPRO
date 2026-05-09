package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.DiarioArea;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface DiarioAreasRepository extends JpaRepository<DiarioArea, Long>, NamedEntityRepository<DiarioArea> {

	Optional<DiarioArea> findByNomeIgnoreCase(String nome);
}
