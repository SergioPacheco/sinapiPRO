package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Estado;
import br.edu.ifrn.sinapiPRO.repository.Estados;
import br.edu.ifrn.sinapiPRO.service.exception.NomeEstadoJaCadastradoException;

@Service
public class EstadoService {

	@Autowired
	private Estados estados;
	
	@Transactional
	public Estado salvar(Estado estado) {
		Optional<Estado> estadoOptional = estados.findByNomeIgnoreCase(estado.getNome());
		if (estadoOptional.isPresent()) {
			throw new NomeEstadoJaCadastradoException("Nome do estado já cadastrado");
		}
		return estados.saveAndFlush(estado);
	}
	
}
