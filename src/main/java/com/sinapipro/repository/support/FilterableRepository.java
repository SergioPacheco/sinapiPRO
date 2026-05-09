package com.sinapipro.repository.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FilterableRepository<T, F> {

	Page<T> filtrar(F filtro, Pageable pageable);
}
