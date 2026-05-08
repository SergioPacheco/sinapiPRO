package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.ContaBancaria;
import br.edu.ifrn.sinapiPRO.model.MovimentoBancario;
import br.edu.ifrn.sinapiPRO.repository.ContasBancariasRepository;
import br.edu.ifrn.sinapiPRO.repository.MovimentosBancariosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ResourceNotFoundException;

/**
 * Lógica de negócio para conciliação bancária.
 *
 * REGRAS (práticas contábeis brasileiras — NBC TG 03):
 *
 * 1. CONCILIAÇÃO:
 *    - Compara movimentos do sistema com extrato bancário
 *    - Marca movimentos como CONCILIADOS quando confirmados no extrato
 *    - Identifica diferenças (movimentos no sistema sem extrato e vice-versa)
 *
 * 2. SALDO CONCILIADO:
 *    - Soma apenas movimentos marcados como conciliados
 *    - Diferença = saldo_sistema - saldo_extrato
 *
 * 3. PROCESSO:
 *    - Usuário informa saldo do extrato bancário na data
 *    - Sistema calcula saldo dos movimentos conciliados
 *    - Diferença indica movimentos pendentes de conciliação
 */
@Service
public class ConciliacaoBancariaService {

    private final MovimentosBancariosRepository movimentoRepository;
    private final ContasBancariasRepository contaRepository;

    public ConciliacaoBancariaService(
            MovimentosBancariosRepository movimentoRepository,
            ContasBancariasRepository contaRepository) {
        this.movimentoRepository = movimentoRepository;
        this.contaRepository = contaRepository;
    }

    /**
     * Marca um movimento como conciliado.
     */
    @Transactional
    public void conciliar(Long codigoMovimento) {
        MovimentoBancario movimento = buscarMovimento(codigoMovimento);
        movimento.setConciliado(true);
        movimentoRepository.saveAndFlush(movimento);
    }

    /**
     * Desmarca um movimento como conciliado.
     */
    @Transactional
    public void desconciliar(Long codigoMovimento) {
        MovimentoBancario movimento = buscarMovimento(codigoMovimento);
        movimento.setConciliado(false);
        movimentoRepository.saveAndFlush(movimento);
    }

    /**
     * Concilia em lote: marca todos os movimentos do período como conciliados.
     */
    @Transactional
    public int conciliarLote(Long codigoConta, LocalDate inicio, LocalDate fim) {
        List<MovimentoBancario> movimentos = movimentoRepository
                .findByContaBancariaCodigoAndDataMovimentoBetweenOrderByDataMovimentoAsc(
                        codigoConta, inicio, fim);

        movimentos.forEach(m -> m.setConciliado(true));
        movimentoRepository.saveAll(movimentos);
        movimentoRepository.flush();
        return movimentos.size();
    }

    /**
     * Calcula o saldo conciliado de uma conta até uma data.
     * Saldo conciliado = soma dos movimentos marcados como conciliados.
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoConciliado(Long codigoConta, LocalDate ate) {
        return calcularSaldoConciliado(buscarMovimentosAteData(codigoConta, ate));
    }

    private BigDecimal calcularSaldoConciliado(List<MovimentoBancario> movimentos) {
        BigDecimal saldo = BigDecimal.ZERO;
        for (MovimentoBancario movimento : movimentos) {
            if (!movimento.isConciliado()) {
                continue;
            }
            if ("CREDITO".equals(movimento.getTipo())) {
                saldo = saldo.add(movimento.getValor());
            } else {
                saldo = saldo.subtract(movimento.getValor());
            }
        }
        return saldo;
    }

    /**
     * Retorna movimentos não conciliados de uma conta no período.
     */
    @Transactional(readOnly = true)
    public List<MovimentoBancario> findNaoConciliados(Long codigoConta, LocalDate inicio, LocalDate fim) {
        return movimentoRepository
                .findByContaBancariaCodigoAndDataMovimentoBetweenOrderByDataMovimentoAsc(
                        codigoConta, inicio, fim)
                .stream()
                .filter(m -> !m.isConciliado())
                .collect(Collectors.toList());
    }

    /**
     * Gera resumo da conciliação: saldo sistema vs saldo extrato informado.
     */
    @Transactional(readOnly = true)
    public ResumoConciliacao gerarResumo(Long codigoConta, LocalDate ate, BigDecimal saldoExtrato) {
        ContaBancaria conta = buscarConta(codigoConta);
        List<MovimentoBancario> movimentos = buscarMovimentosAteData(codigoConta, ate);
        long movimentosConciliados = movimentos.stream()
                .filter(MovimentoBancario::isConciliado)
                .count();

        ResumoConciliacao resumo = new ResumoConciliacao();
        resumo.setConta(conta.getBanco() + " — " + conta.getConta());
        resumo.setSaldoSistema(conta.getSaldoAtual());
        resumo.setSaldoConciliado(calcularSaldoConciliado(movimentos));
        resumo.setSaldoExtrato(saldoExtrato);
        resumo.setDiferenca(resumo.getSaldoConciliado().subtract(saldoExtrato != null ? saldoExtrato : BigDecimal.ZERO));
        resumo.setTotalMovimentos(movimentos.size());
        resumo.setMovimentosConciliados((int) movimentosConciliados);
        resumo.setMovimentosPendentes((int) (movimentos.size() - movimentosConciliados));
        return resumo;
    }

    private MovimentoBancario buscarMovimento(Long codigoMovimento) {
        return movimentoRepository.findById(codigoMovimento)
                .orElseThrow(() -> new ResourceNotFoundException("Movimento não encontrado."));
    }

    private ContaBancaria buscarConta(Long codigoConta) {
        return contaRepository.findById(codigoConta)
                .orElseThrow(() -> new ResourceNotFoundException("Conta bancária não encontrada."));
    }

    private List<MovimentoBancario> buscarMovimentosAteData(Long codigoConta, LocalDate ate) {
        return movimentoRepository.findByContaBancariaCodigoOrderByDataMovimentoDesc(codigoConta)
                .stream()
                .filter(movimento -> !movimento.getDataMovimento().isAfter(ate))
                .collect(Collectors.toList());
    }

    public static class ResumoConciliacao {

        private String conta;
        private BigDecimal saldoSistema;
        private BigDecimal saldoConciliado;
        private BigDecimal saldoExtrato;
        private BigDecimal diferenca;
        private int totalMovimentos;
        private int movimentosConciliados;
        private int movimentosPendentes;

        public String getConta() {
            return conta;
        }

        public void setConta(String conta) {
            this.conta = conta;
        }

        public BigDecimal getSaldoSistema() {
            return saldoSistema;
        }

        public void setSaldoSistema(BigDecimal saldoSistema) {
            this.saldoSistema = saldoSistema;
        }

        public BigDecimal getSaldoConciliado() {
            return saldoConciliado;
        }

        public void setSaldoConciliado(BigDecimal saldoConciliado) {
            this.saldoConciliado = saldoConciliado;
        }

        public BigDecimal getSaldoExtrato() {
            return saldoExtrato;
        }

        public void setSaldoExtrato(BigDecimal saldoExtrato) {
            this.saldoExtrato = saldoExtrato;
        }

        public BigDecimal getDiferenca() {
            return diferenca;
        }

        public void setDiferenca(BigDecimal diferenca) {
            this.diferenca = diferenca;
        }

        public int getTotalMovimentos() {
            return totalMovimentos;
        }

        public void setTotalMovimentos(int totalMovimentos) {
            this.totalMovimentos = totalMovimentos;
        }

        public int getMovimentosConciliados() {
            return movimentosConciliados;
        }

        public void setMovimentosConciliados(int movimentosConciliados) {
            this.movimentosConciliados = movimentosConciliados;
        }

        public int getMovimentosPendentes() {
            return movimentosPendentes;
        }

        public void setMovimentosPendentes(int movimentosPendentes) {
            this.movimentosPendentes = movimentosPendentes;
        }

    }
}
