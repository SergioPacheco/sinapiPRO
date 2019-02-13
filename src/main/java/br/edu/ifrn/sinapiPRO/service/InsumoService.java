package br.edu.ifrn.sinapiPRO.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Insumo;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.ProibidoAlterarTabelaSinapiException;

@Service
public class InsumoService {

	@Autowired
	private InsumosRepository insumosRepository;
	
	@Transactional
	public Insumo salvar(Insumo insumo) {
		
		return insumosRepository.saveAndFlush(insumo);
	}
	 
	@Transactional
	public void excluir(Insumo insumo) {
		try {
			insumosRepository.delete(insumo);
			insumosRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Insumo. Já foi usado em algum orçamento.");
		}
	}
	 
}