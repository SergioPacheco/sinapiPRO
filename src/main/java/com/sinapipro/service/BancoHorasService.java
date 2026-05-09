package com.sinapipro.service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.BancoHoras;
import com.sinapipro.model.MovimentacaoHora;
import com.sinapipro.repository.BancoHorasRepository;
import com.sinapipro.repository.MovimentacoesHoraRepository;

@Service
public class BancoHorasService {

    private final BancoHorasRepository bancoHorasRepository;
    private final MovimentacoesHoraRepository movimentacoesRepository;

    public BancoHorasService(
            BancoHorasRepository bancoHorasRepository,
            MovimentacoesHoraRepository movimentacoesRepository) {
        this.bancoHorasRepository = bancoHorasRepository;
        this.movimentacoesRepository = movimentacoesRepository;
    }

    @Transactional
    public MovimentacaoHora registrarMovimentacao(MovimentacaoHora movimentacao) {
        movimentacoesRepository.saveAndFlush(movimentacao);
        recalcularSaldo(movimentacao.getFuncionario().getCodigo(),
                movimentacao.getCompetencia().getCodigo());
        return movimentacao;
    }

    @Transactional
    public void recalcularSaldo(Long codigoFuncionario, Long codigoCompetencia) {
        List<MovimentacaoHora> movimentos = movimentacoesRepository
                .findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataMovimentacaoAsc(
                        codigoFuncionario, codigoCompetencia);

        BigDecimal credito = movimentos.stream()
                .filter(m -> "CREDITO".equals(m.getTipo()) || "EXTRA".equals(m.getTipo()))
                .map(MovimentacaoHora::getHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal debito = movimentos.stream()
                .filter(m -> "DEBITO".equals(m.getTipo()))
                .map(MovimentacaoHora::getHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BancoHoras banco = bancoHorasRepository
                .findByFuncionarioCodigoAndCompetenciaCodigo(codigoFuncionario, codigoCompetencia)
                .orElse(new BancoHoras());

        banco.setHorasCredito(credito);
        banco.setHorasDebito(debito);
        banco.setSaldo(credito.subtract(debito));
        bancoHorasRepository.saveAndFlush(banco);
    }

    @Transactional(readOnly = true)
    public List<BancoHoras> findByCompetencia(Long codigoCompetencia) {
        return bancoHorasRepository.findByCompetenciaCodigoOrderByFuncionarioNomeAsc(codigoCompetencia);
    }

    @Transactional(readOnly = true)
    public List<MovimentacaoHora> findMovimentacoes(Long codigoFuncionario, Long codigoCompetencia) {
        return movimentacoesRepository
                .findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataMovimentacaoAsc(
                        codigoFuncionario, codigoCompetencia);
    }
}
