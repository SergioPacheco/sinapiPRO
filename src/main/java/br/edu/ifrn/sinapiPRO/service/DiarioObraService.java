package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.DiarioObra;
import br.edu.ifrn.sinapiPRO.repository.DiarioObraRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractObraScopedCrudService;

@Service
public class DiarioObraService extends AbstractObraScopedCrudService<DiarioObra, DiarioObraRepository> {

	public DiarioObraService(DiarioObraRepository repository) {
		super(repository, "Impossível apagar o diário.", "Diário não encontrado.");
	}

	@Transactional(readOnly = true)
	public DiarioObra buscarComItens(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
