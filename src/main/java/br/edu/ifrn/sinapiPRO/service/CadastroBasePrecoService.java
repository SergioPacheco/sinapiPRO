package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.BasePrecos;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeBasePrecoJaCadastradaException;


@Service
public class CadastroBasePrecoService {
	
	@Autowired
	private BasePrecos basePrecos;
	
	@Transactional 
	public BasePreco salvar(BasePreco basePreco){
		
		Optional<BasePreco> basePrecoOptional = basePrecos.findByNomeIgnoreCase(basePreco.getNome());
		if(basePrecoOptional.isPresent()){
			throw new NomeBasePrecoJaCadastradaException("Nome da base já cadastrada");
		}
		return basePrecos.saveAndFlush(basePreco);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			basePrecos.deleteById(codigo);
			basePrecos.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Base. Já foi usado em algum orçamento.");

		}
	}

}