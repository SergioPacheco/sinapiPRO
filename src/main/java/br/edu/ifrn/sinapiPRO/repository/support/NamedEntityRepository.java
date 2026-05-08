package br.edu.ifrn.sinapiPRO.repository.support;

import java.util.Optional;

public interface NamedEntityRepository<T> {

	Optional<T> findByNomeIgnoreCase(String nome);
}
