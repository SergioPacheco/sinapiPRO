package br.edu.ifrn.sinapiPRO.service.support;

import java.util.List;

public interface CrudListService<T> {

	T salvar(T entidade);

	void excluir(Long codigo);

	List<T> findAll();

	T buscarPorCodigo(Long codigo);
}
