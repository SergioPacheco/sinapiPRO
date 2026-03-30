package br.edu.ifrn.sinapiPRO.service;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.HistoricoBancario;
import br.edu.ifrn.sinapiPRO.repository.HistoricosBancariosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class HistoricoBancarioService {
	@Autowired
	private HistoricosBancariosRepository repository;
	@Transactional
	public HistoricoBancario salvar(HistoricoBancario h) {
		return repository.saveAndFlush(h);
	}

	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar.");
		}
	}

	public List<HistoricoBancario> findAll() {
		return repository.findAll();
	}

	public HistoricoBancario getOne(Long c) {
		return repository.getOne(c);
	}
}
