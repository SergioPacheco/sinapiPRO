package br.edu.ifrn.sinapiPRO.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.DocumentoGed;
import br.edu.ifrn.sinapiPRO.repository.DocumentosGedRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractObraScopedCrudService;

@Service
public class GedService extends AbstractObraScopedCrudService<DocumentoGed, DocumentosGedRepository> {

	private final DocumentosGedRepository repository;

	public GedService(DocumentosGedRepository repository) {
		super(repository, "Impossível apagar o documento.", "Documento não encontrado.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public DocumentoGed salvar(DocumentoGed documentoGed) {
		if (documentoGed.getDataUpload() == null) {
			documentoGed.setDataUpload(LocalDateTime.now());
		}
		return repository.saveAndFlush(documentoGed);
	}

}
