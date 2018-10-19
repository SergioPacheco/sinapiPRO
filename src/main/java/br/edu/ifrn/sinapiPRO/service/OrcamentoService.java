package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.repository.OrcamentosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeClasseJaCadastradaException;

@Service
public class OrcamentoService {
	
	@Autowired
	private OrcamentosRepository orcamentoRepository;
	
	@Transactional
	public Orcamento salvar(Orcamento orcamento){
		
		Optional<Orcamento> orcaOptional = orcamentoRepository.findByNomeIgnoreCase(orcamento.getNome());
		if(orcaOptional.isPresent() && orcamento.isNovo()){
			throw new NomeClasseJaCadastradaException("Nome do classe já cadastrado");
		}
		return orcamentoRepository.saveAndFlush(orcamento);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			orcamentoRepository.deleteById(codigo);
			orcamentoRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar Orcamento. Já foi usado alguma composição.");

		}
	}

}
 