package br.edu.ifrn.sinapiPRO.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.repository.EstadosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class EstadoService {
	
	private final EstadosRepository estadosRepository;
	
	@Autowired
	public EstadoService (EstadosRepository estadosRepository) {
		this.estadosRepository = estadosRepository;
	}
	 
	
	@Transactional
	public Estado salvar(Estado estado) {
		Optional<Estado> estadoOptional = estadosRepository.findByNomeIgnoreCase(estado.getNome());
		if (estadoOptional.isPresent()) {
			throw new JaCadastradoException("Nome do estado já cadastrado");
		}
		return estadosRepository.saveAndFlush(estado);
	}
	
	public List<Estado> findAll() {
		return estadosRepository.findAll();
	}
	
	
}
