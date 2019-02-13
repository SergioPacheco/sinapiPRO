package br.edu.ifrn.sinapiPRO.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.OrcamentoItem;
import br.edu.ifrn.sinapiPRO.repository.OrcamentoItemsRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class OrcamentoItemService {
	
	@Autowired
	private OrcamentoItemsRepository OrcamentoItemRepository;
	
	@Transactional
	public void salvar(OrcamentoItem orcamentoItem){
		OrcamentoItemRepository.save(orcamentoItem);
	}
	
	@Transactional
	public void excluir(Long codigo) {
		try {
			OrcamentoItemRepository.deleteById(codigo);  
			OrcamentoItemRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar o item do orçamento.");

		}
	}
}
