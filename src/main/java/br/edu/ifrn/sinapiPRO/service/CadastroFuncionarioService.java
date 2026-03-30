package br.edu.ifrn.sinapiPRO.service;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Funcionario;
import br.edu.ifrn.sinapiPRO.repository.FuncionariosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncionarioFilter;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroFuncionarioService {
	@Autowired
	private FuncionariosRepository repository;
	@Transactional
	public Funcionario salvar(Funcionario f) {
		return repository.saveAndFlush(f);
	}

	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso.");
		}
	}

	@Transactional(readOnly = true)
	public Page<Funcionario> filtrar(FuncionarioFilter f, Pageable p) {
		return repository.filtrar(f, p);
	}

	public List<Funcionario> findAll() {
		return repository.findAll();
	}

	public Funcionario getOne(Long c) {
		return repository.getOne(c);
	}
}
