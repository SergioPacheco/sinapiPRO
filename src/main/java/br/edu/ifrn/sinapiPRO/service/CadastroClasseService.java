package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Classe;
import br.edu.ifrn.sinapiPRO.repository.Classes;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeClasseJaCadastradaException;

@Service
public class CadastroClasseService {
	
	@Autowired
	private Classes classes;
	
	@Transactional//Controlar os classes
	public Classe salvar(Classe classe){
		
		Optional<Classe> classeOptional = classes.findByNomeIgnoreCase(classe.getNome());
		if(classeOptional.isPresent()){
			throw new NomeClasseJaCadastradaException("Nome do classe já cadastrado");
		}
		return classes.saveAndFlush(classe);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			classes.deleteById(codigo);
			classes.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar classe. Já foi usado em alguma cerveja.");

		}
	}

}