package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeClasseJaCadastradaException;

@Service
public class BaseInsumoService  {
	
	@Autowired
	private BaseInsumosRepository baseInsumosRepository;
	
	@Transactional 
	public BaseInsumo salvar(BaseInsumo baseInsumo){
		
		Optional<BaseInsumo> baseInsumoExistente = baseInsumosRepository.findByNomeIgnoreCase(baseInsumo.getNome());
		
		if(baseInsumoExistente.isPresent() && baseInsumo.isNova()) {
			throw new NomeClasseJaCadastradaException("Nome da Base Já Cadastrada");
		}
		return baseInsumosRepository.saveAndFlush(baseInsumo);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			baseInsumosRepository.deleteById(codigo);
			baseInsumosRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar base. Já foi usado em alguma cerveja.");

		}
	}

}
 