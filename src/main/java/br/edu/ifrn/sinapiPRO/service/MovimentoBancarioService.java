package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.ContaBancaria;
import br.edu.ifrn.sinapiPRO.model.MovimentoBancario;
import br.edu.ifrn.sinapiPRO.repository.ContasBancariasRepository;
import br.edu.ifrn.sinapiPRO.repository.MovimentosBancariosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class MovimentoBancarioService {
	@Autowired
	private MovimentosBancariosRepository repository;
	@Autowired
	private ContasBancariasRepository contaRepository;
	@Transactional
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
	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar o movimento.");
		}
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
