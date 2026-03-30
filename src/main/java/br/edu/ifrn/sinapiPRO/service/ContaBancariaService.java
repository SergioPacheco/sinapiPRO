package br.edu.ifrn.sinapiPRO.service;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.ContaBancaria;
import br.edu.ifrn.sinapiPRO.repository.ContasBancariasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class ContaBancariaService {
	@Autowired
	private ContasBancariasRepository repository;
	@Transactional
	public ContaBancaria salvar(ContaBancaria c) {
		return repository.saveAndFlush(c);
	}

	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar. Possui movimentos vinculados.");
		}
	}

	public List<ContaBancaria> findAll() {
		return repository.findAll();
	}

	public List<ContaBancaria> findAtivas() {
		return repository.findByAtivaTrue();
	}

	public ContaBancaria getOne(Long c) {
		return repository.getOne(c);
	}
}
