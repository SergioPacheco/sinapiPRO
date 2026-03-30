package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.repository.ItemRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.AtualFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ItemService {
	
	@Autowired
	private ItemRepository itemRepository;
	
	@Transactional 
	public Item salvar(Item item){
		//TODO: testar quantidade não pode ser negativo ou zero
		
		return itemRepository.saveAndFlush(item);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			itemRepository.deleteById(codigo);
			itemRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar o item.");
		}
	}
	
	public Page<Item> filtrar(AtualFilter filtro, Pageable pageable) {
		return itemRepository.filtrar(filtro, pageable);
	}
	
	public List<Etapa> findEtapasOrcamento(Long codigo) { 
		return itemRepository.findEtapasOrcamento(codigo);
	}

	public BigDecimal somaValorMaoObra(Long codigo) {
		return itemRepository.somaValorMaoObra(codigo);
	}

	public BigDecimal somaValorMaterial(Long codigo) {
		return itemRepository.somaValorMaterial(codigo);
	}

	public BigDecimal somaValorEquipamento(Long codigo) {
		return itemRepository.somaValorEquipamento(codigo);
	}
	
	public BigDecimal somaValorTotal(Long codigo) {
		return itemRepository.somaValorTotal(codigo);
	}
	
}