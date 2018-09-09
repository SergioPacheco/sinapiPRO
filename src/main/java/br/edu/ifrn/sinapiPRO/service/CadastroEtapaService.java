package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.repository.Etapas;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeEtapaJaCadastradaException;

@Service
public class CadastroEtapaService {
	
	@Autowired
	private Etapas etapas;
	
	@Transactional 
	public Etapa salvar(Etapa etapa){
		
		Optional<Etapa> etapaOptional = etapas.findByNomeIgnoreCase(etapa.getNome());
		if(etapaOptional.isPresent()){
			throw new NomeEtapaJaCadastradaException("Nome do etapa já cadastrado");
		}
		return etapas.saveAndFlush(etapa);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			etapas.deleteById(codigo);
			etapas.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar etapa. Já foi usado em alguma cerveja.");

		}
	}
}