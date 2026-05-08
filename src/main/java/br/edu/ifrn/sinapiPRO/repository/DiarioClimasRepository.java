package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.DiarioClima;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface DiarioClimasRepository extends JpaRepository<DiarioClima, Long>, NamedEntityRepository<DiarioClima> {

	Optional<DiarioClima> findByNomeIgnoreCase(String nome);
}
