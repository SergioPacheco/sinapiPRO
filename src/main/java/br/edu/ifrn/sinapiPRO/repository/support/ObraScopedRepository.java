package br.edu.ifrn.sinapiPRO.repository.support;

import java.util.List;

public interface ObraScopedRepository<T> {

	List<T> findByObraScopeCodigo(Long codigoObra);
}
