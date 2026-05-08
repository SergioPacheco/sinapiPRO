package br.edu.ifrn.sinapiPRO.repository.support;

import java.util.List;

public interface ClienteOwnedRepository<T> {

	List<T> findByClienteCodigo(Long codigoCliente);
}
