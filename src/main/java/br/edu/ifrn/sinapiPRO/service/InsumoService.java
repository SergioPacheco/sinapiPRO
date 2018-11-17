package br.edu.ifrn.sinapiPRO.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.Insumos;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class InsumoService {

	@Autowired
	private Insumos insumos;
	
	@Transactional
	public void salvar(Insumo insumo) {
		insumos.save(insumo);
	}
	 
	@Transactional
	public void excluir(Insumo insumo) {
		try {
			insumos.delete(insumo);
			insumos.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Insumo. Já foi usado em algum orçamento.");
		}
	}
	 
}