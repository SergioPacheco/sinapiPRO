package br.edu.ifrn.sinapiPRO.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.ComposicaoGrupo;
import br.edu.ifrn.sinapiPRO.repository.ComposicaoGruposRepository;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Service
public class ComposicaoGrupoService {

	@Autowired
	private ComposicaoGruposRepository composicaoGruposRepository;
	
	@Transactional
	public ComposicaoGrupo salvar(ComposicaoGrupo composicaoGrupo) {
		Optional<ComposicaoGrupo> grupoExistente = composicaoGruposRepository.findByNomeAndComposicaoClasse(composicaoGrupo.getNome(), composicaoGrupo.getComposicaoClasse());
		if (grupoExistente.isPresent()) {
			throw new JaCadastradoException("Nome de Grupo já cadastrado");
		}
		
		return composicaoGruposRepository.save(composicaoGrupo);
	}

	
}
