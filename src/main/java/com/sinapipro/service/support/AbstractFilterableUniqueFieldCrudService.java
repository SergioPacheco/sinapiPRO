package com.sinapipro.service.support;

import java.util.Optional;
import java.util.function.Function;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.repository.support.FilterableRepository;

public abstract class AbstractFilterableUniqueFieldCrudService<T, F, R extends JpaRepository<T, Long> & FilterableRepository<T, F>, U>
		extends AbstractUniqueFieldCrudService<T, R, U> implements CrudPageService<T, F> {

	protected AbstractFilterableUniqueFieldCrudService(
			R repository,
			Function<T, Long> idExtractor,
			Function<T, U> uniqueValueExtractor,
			Function<U, Optional<T>> finder,
			String duplicateMessage,
			String deleteConstraintMessage,
			String notFoundMessage) {
		super(repository, idExtractor, uniqueValueExtractor, finder, duplicateMessage, deleteConstraintMessage, notFoundMessage);
	}

	@Override
	public Page<T> filtrar(F filtro, Pageable pageable) {
		return getRepository().filtrar(filtro, pageable);
	}
}
