package com.sinapipro.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.sinapipro.model.Etapa;
import com.sinapipro.repository.EtapasRepository;
import com.sinapipro.repository.filter.EtapaFilter;
import com.sinapipro.service.support.AbstractNamedEntityCrudService;

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
