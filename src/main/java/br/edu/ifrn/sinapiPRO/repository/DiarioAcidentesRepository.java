package br.edu.ifrn.sinapiPRO.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import br.edu.ifrn.sinapiPRO.model.DiarioAcidente;
import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

@Repository
public interface DiarioAcidentesRepository extends JpaRepository<DiarioAcidente, Long>, NamedEntityRepository<DiarioAcidente> {

	Optional<DiarioAcidente> findByNomeIgnoreCase(String nome);
}
