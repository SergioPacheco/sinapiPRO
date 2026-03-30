package br.edu.ifrn.sinapiPRO.service;
import java.util.*;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Departamento;
import br.edu.ifrn.sinapiPRO.repository.DepartamentosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.DepartamentoFilter;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroDepartamentoService {
	@Autowired
	private DepartamentosRepository repository;
	@Transactional
	public Departamento salvar(Departamento e) {
		Optional<Departamento> ex = repository.findByNomeIgnoreCase(e.getNome());
		if (ex.isPresent() && !ex.get().getCodigo().equals(e.getCodigo())) throw new JaCadastradoException("Departamento já cadastrado(a)");
		return repository.saveAndFlush(e);
	}
	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException ex) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<Departamento> filtrar(DepartamentoFilter f, Pageable p) {
		return repository.filtrar(f, p);
	}

	public List<Departamento> findAll() {
		return repository.findAll();
	}

	public Departamento getOne(Long c) {
		return repository.getOne(c);
	}
}
