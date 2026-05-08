package br.edu.ifrn.sinapiPRO.service.support;

import java.util.function.Function;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.repository.support.NamedEntityRepository;

public abstract class AbstractNamedEntityListCrudService<T, R extends JpaRepository<T, Long> & NamedEntityRepository<T>>
		extends AbstractUniqueFieldCrudService<T, R, String> {

	protected AbstractNamedEntityListCrudService(
			R repository,
			Function<T, Long> idExtractor,
			Function<T, String> nameExtractor,
			String duplicateMessage,
			String deleteConstraintMessage,
			String notFoundMessage) {
		super(repository, idExtractor, nameExtractor, repository::findByNomeIgnoreCase, duplicateMessage, deleteConstraintMessage, notFoundMessage);
	}
}
