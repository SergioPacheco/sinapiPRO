package br.edu.ifrn.sinapiPRO.service;
import java.util.*;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Equipamento;
import br.edu.ifrn.sinapiPRO.repository.EquipamentosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class CadastroEquipamentoService {
	@Autowired
	private EquipamentosRepository repository;
	@Transactional
	public Equipamento salvar(Equipamento e) {
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

	public List<Equipamento> findAll() {
		return repository.findAll();
	}

	public Equipamento getOne(Long c) {
		return repository.getOne(c);
	}
}
