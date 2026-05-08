package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.ContaBancaria;
import br.edu.ifrn.sinapiPRO.model.MovimentoBancario;
import br.edu.ifrn.sinapiPRO.repository.ContasBancariasRepository;
import br.edu.ifrn.sinapiPRO.repository.MovimentosBancariosRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractSimpleCrudService;

@Service
public class MovimentoBancarioService extends AbstractSimpleCrudService<MovimentoBancario, MovimentosBancariosRepository> {

	private final MovimentosBancariosRepository repository;
	private final ContasBancariasRepository contaRepository;

	public MovimentoBancarioService(MovimentosBancariosRepository repository, ContasBancariasRepository contaRepository) {
		super(repository, "Impossível apagar o movimento.", "Movimento bancário não encontrado.");
		this.repository = repository;
		this.contaRepository = contaRepository;
	}

	@Transactional
	@Override
	public MovimentoBancario salvar(MovimentoBancario m) {
		ContaBancaria conta = contaRepository.findById(m.getContaBancaria().getCodigo()).orElseThrow(() -> new RuntimeException("Conta não encontrada"));
		BigDecimal novoSaldo = "CREDITO".equals(m.getTipo())
			? conta.getSaldoAtual().add(m.getValor())
			: conta.getSaldoAtual().subtract(m.getValor());
		conta.setSaldoAtual(novoSaldo);
		m.setSaldoApos(novoSaldo);
		contaRepository.saveAndFlush(conta);
		return repository.saveAndFlush(m);
	}

	@Transactional(readOnly = true)
	public List<MovimentoBancario> findByConta(Long codigoConta) {
		return repository.findByContaBancariaCodigoOrderByDataMovimentoDesc(codigoConta);
	}

	@Transactional(readOnly = true)
	public List<MovimentoBancario> findByContaEPeriodo(Long codigoConta, LocalDate inicio, LocalDate fim) {
		return repository.findByContaBancariaCodigoAndDataMovimentoBetweenOrderByDataMovimentoAsc(codigoConta, inicio, fim);
	}
}
