package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Fornecedor;
import br.edu.ifrn.sinapiPRO.repository.FornecedoresRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FornecedorFilter;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class FornecedorService {

	@Autowired
	private FornecedoresRepository repository;

	@Transactional
	public Fornecedor salvar(Fornecedor fornecedor) {
		Optional<Fornecedor> existente = repository.findByNomeIgnoreCase(fornecedor.getNome());
		if (existente.isPresent() && !existente.get().getCodigo().equals(fornecedor.getCodigo())) {
			throw new JaCadastradoException("Fornecedor já cadastrado");
		}
		return repository.saveAndFlush(fornecedor);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			repository.deleteById(codigo);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar fornecedor. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<Fornecedor> filtrar(FornecedorFilter filtro, Pageable pageable) {
		return repository.filtrar(filtro, pageable);
	}

	public List<Fornecedor> findAll() { return repository.findAll(); }
	public Fornecedor getOne(Long codigo) { return repository.getOne(codigo); }
}
