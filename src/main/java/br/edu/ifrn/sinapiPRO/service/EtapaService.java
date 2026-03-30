package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.repository.EtapasRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.EtapaFilter;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class EtapaService {
	
	@Autowired
	private EtapasRepository etapasRepository;
	
	@Transactional 
	public Etapa salvar(Etapa etapa){
		
		Optional<Etapa> etapaOptional = etapasRepository.findByNomeIgnoreCase(etapa.getNome());
		if(etapaOptional.isPresent()){
			throw new JaCadastradoException("Nome do etapa já cadastrado");
		}
		return etapasRepository.saveAndFlush(etapa);
	}

	@Transactional
	public void excluir(Long codigo) {
		try {
			etapasRepository.deleteById(codigo);
			etapasRepository.flush();
		} catch (PersistenceException e) {
			
			throw new ImpossivelExcluirEntidadeException("Impossível apagar etapa. Já foi usado em alguma cerveja.");
		}
	}
	
	@Transactional(readOnly = true)
	public Page<Etapa> filtrar(EtapaFilter filtro, Pageable pageable) {
		return etapasRepository.filtrar(filtro, pageable);
	}

	public List<Etapa> findAll() {
		// TODO Auto-generated method stub
		return etapasRepository.findAll();
	}

	public List<Etapa> findByNomeStartingWithIgnoreCase(String codigoOuNome) {
		// TODO Auto-generated method stub
		return etapasRepository.findByNomeStartingWithIgnoreCase(codigoOuNome);
	}

	public Etapa getOne(Long codigo) {
		// TODO Auto-generated method stub
		return etapasRepository.getOne(codigo);
	}
	
	
	
}