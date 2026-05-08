package br.edu.ifrn.sinapiPRO.service.support;

import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

public abstract class AbstractSimpleCrudService<T, R extends JpaRepository<T, Long>> implements CrudListService<T> {

	private final R repository;
	private final String deleteConstraintMessage;
	private final String notFoundMessage;

	protected AbstractSimpleCrudService(R repository, String deleteConstraintMessage, String notFoundMessage) {
		this.repository = repository;
		this.deleteConstraintMessage = deleteConstraintMessage;
		this.notFoundMessage = notFoundMessage;
	}

	@Override
	@Transactional
	public T salvar(T entidade) {
		return repository.saveAndFlush(entidade);
	}

	@Override
	@Transactional
	public void excluir(Long codigo) {
		T entidade = buscarPorCodigo(codigo);
		try {
			repository.delete(entidade);
			repository.flush();
		} catch (DataIntegrityViolationException | PersistenceException exception) {
			throw new ImpossivelExcluirEntidadeException(deleteConstraintMessage);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<T> findAll() {
		return repository.findAll();
	}

	@Override
	@Transactional(readOnly = true)
	public T buscarPorCodigo(Long codigo) {
		return repository.findById(codigo).orElseThrow(() -> new ResourceNotFoundException(notFoundMessage));
	}

	protected R getRepository() {
		return repository;
	}
}
