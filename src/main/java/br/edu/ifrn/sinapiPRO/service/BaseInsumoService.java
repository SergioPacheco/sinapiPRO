package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.BaseInsumo;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class BaseInsumoService  {
	
	private BaseInsumosRepository baseInsumosRepository;
	
	@Autowired
	public BaseInsumoService(BaseInsumosRepository baseInsumosRepository) {
		this.baseInsumosRepository = baseInsumosRepository;
	}

	@Transactional 
	public BaseInsumo salvar(BaseInsumo baseInsumo){
		
		Optional<BaseInsumo> baseInsumoExistente = baseInsumosRepository.findByNomeIgnoreCase(baseInsumo.getNome());
		
		if(baseInsumoExistente.isPresent() && baseInsumo.isNova()) {
			throw new JaCadastradoException("Nome da Base Já Cadastrada");
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
	
	public List<BaseInsumo> findAll() {
		return baseInsumosRepository.findAll();
	}
}
 