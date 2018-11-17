package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.ClasseComposicao;
import br.edu.ifrn.sinapiPRO.repository.ClassesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeClasseJaCadastradaException;

@Service
public class ClasseComposicaoService {
	
	@Autowired
	private ClassesRepository classes;
	
	@Transactional
	public ClasseComposicao salvar(ClasseComposicao classe){
		
		Optional<ClasseComposicao> classeOptional = classes.findBySiglaIgnoreCase(classe.getNome());
		if(classeOptional.isPresent() && classe.isNova()){
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
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar classe. Já foi usado em alguma composição.");

		}
	}

}
 