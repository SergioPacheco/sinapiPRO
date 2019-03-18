package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoClassesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class ComposicaoClasseService {

	@Autowired
	private ComposicaoClassesRepository composicaoClassesRepository;
	
	@Transactional
	public ComposicaoClasse salvar(ComposicaoClasse composicaoClasse) {
		Optional<ComposicaoClasse> classeExistente = composicaoClassesRepository
				.findBySiglaIgnoreCase(composicaoClasse.getSigla());
		if (classeExistente.isPresent()) {
			throw new JaCadastradoException("Sgila da classe já cadastrado");
		}
		
		return composicaoClassesRepository.save(composicaoClasse);
	}

	@Transactional
	public void excluir(ComposicaoClasse composicaoClasse) {
		try {
			composicaoClassesRepository.delete(composicaoClasse);
			composicaoClassesRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Insumo. Já foi usado em algum orçamento.");
		}
	}
	
	
	public Object findAll() {
		return composicaoClassesRepository.findAll(); 
	}
}