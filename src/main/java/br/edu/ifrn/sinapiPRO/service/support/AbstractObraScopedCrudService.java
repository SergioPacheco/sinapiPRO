package br.edu.ifrn.sinapiPRO.service.support;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.repository.support.ObraScopedRepository;

public abstract class AbstractObraScopedCrudService<T, R extends JpaRepository<T, Long> & ObraScopedRepository<T>>
		extends AbstractSimpleCrudService<T, R> {

	protected AbstractObraScopedCrudService(R repository, String deleteConstraintMessage, String notFoundMessage) {
		super(repository, deleteConstraintMessage, notFoundMessage);
	}

	@Transactional(readOnly = true)
	public List<T> findByObra(Long codigoObra) {
		return getRepository().findByObraScopeCodigo(codigoObra);
	}
}
