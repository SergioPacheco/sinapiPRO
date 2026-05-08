package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.DiarioArea;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface DiarioAreasRepository extends JpaRepository<DiarioArea, Long>, NamedEntityRepository<DiarioArea> {

	Optional<DiarioArea> findByNomeIgnoreCase(String nome);
}
