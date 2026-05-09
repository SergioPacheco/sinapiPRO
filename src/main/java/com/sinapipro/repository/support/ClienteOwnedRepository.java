package com.sinapipro.repository.support;

import java.util.List;

public interface ClienteOwnedRepository<T> {

	List<T> findByClienteCodigo(Long codigoCliente);
}
