package br.edu.ifrn.sinapiPRO.service;

import org.springframework.stereotype.Service;

import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.SubDivisoesInsumoRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.SubDivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.support.AbstractFilterableSimpleCrudService;

@Service
public class CadastroSubDivisaoInsumoService
		extends AbstractFilterableSimpleCrudService<SubDivisaoInsumo, SubDivisaoInsumoFilter, SubDivisoesInsumoRepository> {

	public CadastroSubDivisaoInsumoService(SubDivisoesInsumoRepository repository) {
		super(repository, "Impossível apagar. Já está em uso.", "Sub-divisão não encontrada.");
	}
}
