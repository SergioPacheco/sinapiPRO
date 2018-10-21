package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumos;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeClasseJaCadastradaException;

@Service
public class CadastroBaseInsumoService  {
	
	@Autowired
	private BaseInsumos baseInsumos;
	
	@Transactional 
	public BaseInsumo salvar(BaseInsumo baseInsumo){
		
		Optional<BaseInsumo> baseInsumoExistente = baseInsumos.findByNomeIgnoreCase(baseInsumo.getNome());
		
		if(baseInsumoExistente.isPresent() && baseInsumo.isNova()) {
			throw new NomeClasseJaCadastradaException("Nome da Base Já Cadastrada");
		}
		return baseInsumos.saveAndFlush(baseInsumo);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			baseInsumos.deleteById(codigo);
			baseInsumos.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar base. Já foi usado em alguma cerveja.");

		}
	}

}
 