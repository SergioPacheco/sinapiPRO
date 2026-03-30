package br.edu.ifrn.sinapiPRO.service;
import java.util.*;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Empresa;
import br.edu.ifrn.sinapiPRO.repository.EmpresasRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.EmpresaFilter;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroEmpresaService {
	@Autowired
	private EmpresasRepository repository;
	@Transactional
	public Empresa salvar(Empresa e) {
		Optional<Empresa> ex = repository.findByNomeIgnoreCase(e.getNome());
		if (ex.isPresent() && !ex.get().getCodigo().equals(e.getCodigo())) throw new JaCadastradoException("Empresa já cadastrada");
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
	public Page<Empresa> filtrar(EmpresaFilter f, Pageable p) {
		return repository.filtrar(f, p);
	}

	public List<Empresa> findAll() {
		return repository.findAll();
	}

	public Empresa getOne(Long c) {
		return repository.getOne(c);
	}
}
