package br.edu.ifrn.sinapiPRO.service.support;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.PersistenceException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

public abstract class AbstractUniqueFieldCrudService<T, R extends JpaRepository<T, Long>, U> implements CrudListService<T> {

	private final R repository;
	private final Function<T, Long> idExtractor;
	private final Function<T, U> uniqueValueExtractor;
	private final Function<U, Optional<T>> finder;
	private final String duplicateMessage;
	private final String deleteConstraintMessage;
	private final String notFoundMessage;

	protected AbstractUniqueFieldCrudService(
			R repository,
			Function<T, Long> idExtractor,
			Function<T, U> uniqueValueExtractor,
			Function<U, Optional<T>> finder,
			String duplicateMessage,
			String deleteConstraintMessage,
			String notFoundMessage) {
		this.repository = repository;
		this.idExtractor = idExtractor;
		this.uniqueValueExtractor = uniqueValueExtractor;
		this.finder = finder;
		this.duplicateMessage = duplicateMessage;
		this.deleteConstraintMessage = deleteConstraintMessage;
		this.notFoundMessage = notFoundMessage;
	}

	@Override
	@Transactional
	public T salvar(T entidade) {
		Optional<T> existente = finder.apply(uniqueValueExtractor.apply(entidade));
		if (existente.isPresent() && !Objects.equals(idExtractor.apply(existente.get()), idExtractor.apply(entidade))) {
			throw new JaCadastradoException(duplicateMessage);
		}
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
