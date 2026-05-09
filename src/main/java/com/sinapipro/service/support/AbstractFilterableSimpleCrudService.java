package com.sinapipro.service.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.repository.support.FilterableRepository;

public abstract class AbstractFilterableSimpleCrudService<T, F, R extends JpaRepository<T, Long> & FilterableRepository<T, F>>
		extends AbstractSimpleCrudService<T, R> implements CrudPageService<T, F> {

	protected AbstractFilterableSimpleCrudService(R repository, String deleteConstraintMessage, String notFoundMessage) {
		super(repository, deleteConstraintMessage, notFoundMessage);
	}

	@Override
	public Page<T> filtrar(F filtro, Pageable pageable) {
		return getRepository().filtrar(filtro, pageable);
	}
}
