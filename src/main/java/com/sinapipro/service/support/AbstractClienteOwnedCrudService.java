package com.sinapipro.service.support;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.repository.support.ClienteOwnedRepository;

public abstract class AbstractClienteOwnedCrudService<T, R extends JpaRepository<T, Long> & ClienteOwnedRepository<T>>
		extends AbstractSimpleCrudService<T, R> {

	protected AbstractClienteOwnedCrudService(R repository, String deleteConstraintMessage, String notFoundMessage) {
		super(repository, deleteConstraintMessage, notFoundMessage);
	}

	public List<T> findByCliente(Long codigoCliente) {
		return getRepository().findByClienteCodigo(codigoCliente);
	}
}
