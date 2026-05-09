package com.sinapipro.service.support;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CrudPageService<T, F> extends CrudListService<T> {

	Page<T> filtrar(F filtro, Pageable pageable);
}
