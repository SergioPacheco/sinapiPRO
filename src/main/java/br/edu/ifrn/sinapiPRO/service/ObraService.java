package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Obra;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class ObraService {
	
	@Autowired
	private ObrasRepository obrasRepository;
	
	@Transactional
	public void salvar(Obra obra){
		
		Optional<Obra> obraExistente = obrasRepository.findByCei(obra.getCei());
		
		if(obraExistente.isPresent() && obra.isNova()){
			System.out.println("isNovo: "+obra.isNova()+""+obra.getCodigo());
			System.out.println("clienteExistente: "+ obraExistente.isPresent());
			throw new JaCadastradoException ("CEI já cadastrada!");
		}
		obrasRepository.save(obra);
	}
	
	@Transactional
	public void excluir(Long codigo) {
		try {
			obrasRepository.deleteById(codigo);  
			obrasRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar a obra. Já esta sendo usada em algum orçamento");

		}
	}
}