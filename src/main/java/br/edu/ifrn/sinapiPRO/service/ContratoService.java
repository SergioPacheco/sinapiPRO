package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.repository.ContratosRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractObraScopedCrudService;

@Service
public class ContratoService extends AbstractObraScopedCrudService<Contrato, ContratosRepository> {

	private final ContratosRepository repository;

	public ContratoService(ContratosRepository repository) {
		super(repository, "Impossível apagar. Possui medições vinculadas.", "Contrato não encontrado.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public Contrato salvar(Contrato contrato) {
		BigDecimal total = contrato.getItens().stream()
				.map(item -> {
					BigDecimal valorTotal = item.getQuantidade().multiply(item.getValorUnitario());
					item.setValorTotal(valorTotal);
					return valorTotal;
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		contrato.setValorTotal(total);
		contrato.getItens().forEach(item -> item.setContrato(contrato));
		return repository.saveAndFlush(contrato);
	}

	@Transactional(readOnly = true)
	public Contrato buscarComItens(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
