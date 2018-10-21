package br.edu.ifrn.sinapiPRO.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.repository.ItemsRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ItemService {
	
	@Autowired
	private ItemsRepository itemRepository;
	
	@Transactional
	public void salvar(Item item){
		itemRepository.save(item);
	}
	
	@Transactional
	public void excluir(Long codigo) {
		try {
			itemRepository.deleteById(codigo);  
			itemRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar o item do orçamento.");

		}
	}
}
