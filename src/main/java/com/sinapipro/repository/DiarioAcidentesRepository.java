package com.sinapipro.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.sinapipro.model.DiarioAcidente;
import com.sinapipro.repository.support.NamedEntityRepository;

@Repository
public interface DiarioAcidentesRepository extends JpaRepository<DiarioAcidente, Long>, NamedEntityRepository<DiarioAcidente> {

	Optional<DiarioAcidente> findByNomeIgnoreCase(String nome);
}
