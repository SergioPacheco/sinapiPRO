package br.edu.ifrn.sinapiPRO.service;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class OrcamentoService {
	
	@Autowired
	private OrcamentosRepository orcamentosRepository;
	
	@Transactional
	public void salvar(Orcamento orcamento){
		
		orcamentosRepository.save(orcamento);
	}
 
	@Transactional
	public void excluir(Orcamento orcamento) {
		try {
			orcamentosRepository.delete(orcamento);
			orcamentosRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível excluir Orcamento. Já foi usado alguma composição.");

		}
	}

}
 