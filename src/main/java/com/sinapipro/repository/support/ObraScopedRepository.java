package com.sinapipro.repository.support;

import java.util.List;

public interface ObraScopedRepository<T> {

	List<T> findByObraScopeCodigo(Long codigoObra);
}
