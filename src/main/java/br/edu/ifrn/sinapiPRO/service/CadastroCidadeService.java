package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Cidade;
import br.edu.ifrn.sinapiPRO.repository.Cidades;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.NomeCidadeJaCadastradaException;

@Service
public class CadastroCidadeService {

	@Autowired
	private Cidades cidades;
	
	@Transactional
	public void salvar(Cidade cidade){
		
		Optional<Cidade> cidadeExistente = cidades.findByNomeAndEstado(cidade.getNome(), cidade.getEstado());
		if(cidadeExistente.isPresent() && cidade.isNova()){
			throw new NomeCidadeJaCadastradaException("Nome da cidade já cadastrado");
		}
		cidades.save(cidade);
	}
	
	@Transactional
	public void excluir(Long codigo) {
		try {
			cidades.deleteById(codigo);  
			cidades.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar a cidade. Já foi usado em algum cadastro de orçamento.");
		}
	}
}