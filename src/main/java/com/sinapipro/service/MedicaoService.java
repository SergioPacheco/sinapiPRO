package com.sinapipro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Medicao;
import com.sinapipro.repository.MedicoesRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class MedicaoService extends AbstractSimpleCrudService<Medicao, MedicoesRepository> {

	private final MedicoesRepository repository;
	private final ValidacaoNegocioService validacao;

	public MedicaoService(MedicoesRepository repository, ValidacaoNegocioService validacao) {
		super(repository, "Impossível apagar a medição.", "Medição não encontrada.");
		this.repository = repository;
		this.validacao = validacao;
	}

	@Transactional
	@Override
	public Medicao salvar(Medicao m) {
		if (m.isNovo()) {
			validacao.validarContratoParaMedicao(m.getContrato().getCodigo());
		}
		validacao.validarNumeromedicaoUnico(m.getContrato().getCodigo(), m.getNumero(), m.getCodigo());

		BigDecimal total = m.getItens().stream()
				.map(item -> {
					item.setMedicao(m);
					return item.getValorMedido() != null ? item.getValorMedido() : BigDecimal.ZERO;
				})
				.reduce(BigDecimal.ZERO, BigDecimal::add);
		m.setValorMedido(total);
		return repository.saveAndFlush(m);
	}

	@Transactional(readOnly = true)
	public List<Medicao> findByContrato(Long codigoContrato) {
		return repository.findByContratoCodigoOrderByNumeroAsc(codigoContrato);
	}

	@Transactional(readOnly = true)
	public Medicao buscarComItens(Long codigo) {
		return buscarPorCodigo(codigo);
	}
}
