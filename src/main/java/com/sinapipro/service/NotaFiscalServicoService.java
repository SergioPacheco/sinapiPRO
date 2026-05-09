package com.sinapipro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.NotaFiscalServico;
import com.sinapipro.repository.NotasFiscaisServicoRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class NotaFiscalServicoService extends AbstractSimpleCrudService<NotaFiscalServico, NotasFiscaisServicoRepository> {

	private final NotasFiscaisServicoRepository repository;

	public NotaFiscalServicoService(NotasFiscaisServicoRepository repository) {
		super(repository, "Impossível apagar a nota fiscal.", "Nota fiscal de serviço não encontrada.");
		this.repository = repository;
	}

	@Override
	@Transactional
	public NotaFiscalServico salvar(NotaFiscalServico notaFiscalServico) {
		if (notaFiscalServico.getValorServicos() != null && notaFiscalServico.getAliquotaIss() != null) {
			BigDecimal iss = notaFiscalServico.getValorServicos()
					.multiply(notaFiscalServico.getAliquotaIss())
					.divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
			notaFiscalServico.setValorIss(iss);
			notaFiscalServico.setValorLiquido(notaFiscalServico.getValorServicos().subtract(iss));
		}
		return repository.saveAndFlush(notaFiscalServico);
	}
}
