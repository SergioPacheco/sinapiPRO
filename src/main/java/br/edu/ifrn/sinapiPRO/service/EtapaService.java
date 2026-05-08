package br.edu.ifrn.sinapiPRO.service;

import java.util.List;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.Etapa;
import br.edu.ifrn.sinapiPRO.repository.EtapasRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.EtapaFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractNamedEntityCrudService;

@Service
public class EtapaService extends AbstractNamedEntityCrudService<Etapa, EtapaFilter, EtapasRepository> {

	private final EtapasRepository etapasRepository;

	public EtapaService(EtapasRepository etapasRepository) {
		super(
				etapasRepository,
				Etapa::getCodigo,
				Etapa::getNome,
				"Nome da etapa já cadastrado",
				"Impossível apagar etapa. Já foi usado em alguma cerveja.",
				"Etapa não encontrada.");
		this.etapasRepository = etapasRepository;
	}

	public List<Etapa> findByNomeStartingWithIgnoreCase(String codigoOuNome) {
		return etapasRepository.findByNomeStartingWithIgnoreCase(codigoOuNome);
	}
}
