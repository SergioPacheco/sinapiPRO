package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.repository.BasePrecosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class BasePrecoService {
	
	private BasePrecosRepository basePrecosRepository;
	
	@Autowired
	public BasePrecoService (BasePrecosRepository basePrecoRepository) {
		this.basePrecosRepository = basePrecoRepository;
	}
	
	@Transactional 
	public BasePreco salvar(BasePreco basePreco){
		
		Optional<BasePreco> basePrecoOptional = basePrecosRepository.findByNomeIgnoreCase(basePreco.getNome());
		if(basePrecoOptional.isPresent() && basePreco.isNova()){
			throw new JaCadastradoException("Nome da base já cadastrada");
		}
		return basePrecosRepository.saveAndFlush(basePreco);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			basePrecosRepository.deleteById(codigo);
			basePrecosRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Base. Já foi usado em algum orçamento.");
		}
	}
	
	public List<BasePreco> findAll() {
		return basePrecosRepository.findAll();
	}
}