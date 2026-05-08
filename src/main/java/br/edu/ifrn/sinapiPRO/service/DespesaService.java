package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.Despesa;
import br.edu.ifrn.sinapiPRO.repository.DespesasRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class DespesaService extends AbstractSimpleCrudService<Despesa, DespesasRepository> {

	private final DespesasRepository repository;

	public DespesaService(DespesasRepository repository) {
		super(repository, "Impossível apagar a despesa.", "Despesa não encontrada.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public Despesa salvar(Despesa despesa) {
		despesa.getPagamentos().forEach(pagamento -> pagamento.setDespesa(despesa));
		BigDecimal totalPago = despesa.getPagamentos().stream()
				.map(pagamento -> pagamento.getValorPago())
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		if (totalPago.compareTo(despesa.getValor()) >= 0) {
			despesa.setSituacao("PAGA");
		} else if (totalPago.signum() > 0) {
			despesa.setSituacao("PARCIAL");
		}
		return repository.saveAndFlush(despesa);
	}

	@Transactional(readOnly = true)
	public List<Despesa> findAbertas() {
		return repository.findBySituacaoOrderByDataVencimentoAsc("ABERTA");
	}

	@Transactional(readOnly = true)
	public Despesa buscarComPagamentos(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
