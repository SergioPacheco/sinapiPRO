package com.sinapipro.service.support;

import java.util.List;

public interface CrudListService<T> {

	T salvar(T entidade);

	void excluir(Long codigo);

	List<T> findAll();

	T buscarPorCodigo(Long codigo);
}
