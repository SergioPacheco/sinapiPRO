package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.BancoHoras;
import br.edu.ifrn.sinapiPRO.model.Competencia;
import br.edu.ifrn.sinapiPRO.model.Funcionario;
import br.edu.ifrn.sinapiPRO.model.MovimentacaoHora;
import br.edu.ifrn.sinapiPRO.repository.BancoHorasRepository;
import br.edu.ifrn.sinapiPRO.repository.CompetenciasRepository;
import br.edu.ifrn.sinapiPRO.repository.FuncionariosRepository;
import br.edu.ifrn.sinapiPRO.repository.MovimentacoesHoraRepository;

/**
 * Lógica de negócio para banco de horas.
 *
 * REGRAS (CLT + práticas de RH):
 *
 * 1. ENCERRAMENTO DE COMPETÊNCIA:
 *    - Calcula saldo final de cada funcionário na competência
 *    - Transfere saldo positivo para a próxima competência (crédito)
 *    - Saldo negativo: desconta do funcionário (débito na próxima)
 *    - Marca competência como ENCERRADA
 *
 * 2. SALDO:
 *    - Crédito: horas extras trabalhadas (tipo EXTRA ou CREDITO)
 *    - Débito: horas compensadas/faltadas (tipo DEBITO)
 *    - Saldo = crédito - débito
 *
 * 3. LIMITE DE BANCO (CLT art. 59):
 *    - Máximo de 10 horas extras por semana
 *    - Banco deve ser zerado em até 6 meses
 *    - Alerta quando saldo > 40 horas (limite prático)
 *
 * Referência: CLT Art. 59, Acordo de Compensação de Horas.
 */
@Service
public class EncerrarCompetenciaService {

    private static final BigDecimal LIMITE_ALERTA_HORAS = new BigDecimal("40");

    private final CompetenciasRepository competenciaRepository;
    private final BancoHorasRepository bancoHorasRepository;
    private final MovimentacoesHoraRepository movimentacoesRepository;
    private final FuncionariosRepository funcionariosRepository;

    public EncerrarCompetenciaService(
            CompetenciasRepository competenciaRepository,
            BancoHorasRepository bancoHorasRepository,
            MovimentacoesHoraRepository movimentacoesRepository,
            FuncionariosRepository funcionariosRepository) {
        this.competenciaRepository = competenciaRepository;
        this.bancoHorasRepository = bancoHorasRepository;
        this.movimentacoesRepository = movimentacoesRepository;
        this.funcionariosRepository = funcionariosRepository;
    }

    /**
     * Encerra uma competência e transfere saldos para a próxima.
     *
     * @param codigoCompetencia competência a encerrar
     * @return relatório do encerramento
     */
    @Transactional
    public RelatorioEncerramento encerrarCompetencia(Long codigoCompetencia) {
        Competencia competencia = competenciaRepository.findById(codigoCompetencia)
                .orElseThrow(() -> new RuntimeException("Competência não encontrada"));

        if (competencia.isEncerrada()) {
            throw new RuntimeException("Competência já está encerrada.");
        }

        // Busca ou cria a próxima competência
        int proximoMes = competencia.getMes() == 12 ? 1 : competencia.getMes() + 1;
        int proximoAno = competencia.getMes() == 12 ? competencia.getAno() + 1 : competencia.getAno();

        Competencia proximaCompetencia = competenciaRepository
                .findByMesAndAno(proximoMes, proximoAno)
                .orElseGet(() -> {
                    Competencia nova = new Competencia();
                    nova.setMes(proximoMes);
                    nova.setAno(proximoAno);
                    nova.setDescricao(String.format("%02d/%d", proximoMes, proximoAno));
                    return competenciaRepository.saveAndFlush(nova);
                });

        RelatorioEncerramento relatorio = new RelatorioEncerramento();
        relatorio.setCompetencia(competencia.getLabel());
        relatorio.setProximaCompetencia(proximaCompetencia.getLabel());

        // Processa cada funcionário ativo
        List<Funcionario> funcionarios = funcionariosRepository.findByAtivoTrue();
        int transferidos = 0;
        int alertas = 0;

        for (Funcionario funcionario : funcionarios) {
            Optional<BancoHoras> bancoOpt = bancoHorasRepository
                    .findByFuncionarioCodigoAndCompetenciaCodigo(
                            funcionario.getCodigo(), codigoCompetencia);

            if (bancoOpt.isEmpty()) continue;

            BancoHoras banco = bancoOpt.get();
            BigDecimal saldo = banco.getSaldo();

            if (saldo.signum() == 0) continue;

            // Transfere saldo para próxima competência
            MovimentacaoHora transferencia = new MovimentacaoHora();
            transferencia.setFuncionario(funcionario);
            transferencia.setCompetencia(proximaCompetencia);
            transferencia.setDataMovimentacao(LocalDate.of(proximoAno, proximoMes, 1));
            transferencia.setDescricao("Saldo transferido da competência " + competencia.getLabel());

            if (saldo.signum() > 0) {
                // Saldo positivo → crédito na próxima
                transferencia.setTipo("CREDITO");
                transferencia.setHoras(saldo);
            } else {
                // Saldo negativo → débito na próxima
                transferencia.setTipo("DEBITO");
                transferencia.setHoras(saldo.abs());
            }

            movimentacoesRepository.save(transferencia);

            // Recalcula banco da próxima competência
            recalcularBanco(funcionario, proximaCompetencia);

            transferidos++;

            // Alerta se saldo acumulado > limite
            if (saldo.compareTo(LIMITE_ALERTA_HORAS) > 0) {
                alertas++;
                relatorio.getAlertas().add(String.format(
                        "%s: saldo de %.1f horas excede o limite de %.0f horas",
                        funcionario.getNome(), saldo, LIMITE_ALERTA_HORAS));
            }
        }

        // Encerra a competência
        competencia.setEncerrada(true);
        competenciaRepository.saveAndFlush(competencia);
        movimentacoesRepository.flush();

        relatorio.setFuncionariosTransferidos(transferidos);
        relatorio.setAlertas(relatorio.getAlertas());
        relatorio.setAlertasCount(alertas);
        return relatorio;
    }

    private void recalcularBanco(Funcionario funcionario, Competencia competencia) {
        List<MovimentacaoHora> movimentos = movimentacoesRepository
                .findByFuncionarioCodigoAndCompetenciaCodigoOrderByDataMovimentacaoAsc(
                        funcionario.getCodigo(), competencia.getCodigo());

        BigDecimal credito = movimentos.stream()
                .filter(m -> "CREDITO".equals(m.getTipo()) || "EXTRA".equals(m.getTipo()))
                .map(MovimentacaoHora::getHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal debito = movimentos.stream()
                .filter(m -> "DEBITO".equals(m.getTipo()))
                .map(MovimentacaoHora::getHoras)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BancoHoras banco = bancoHorasRepository
                .findByFuncionarioCodigoAndCompetenciaCodigo(
                        funcionario.getCodigo(), competencia.getCodigo())
                .orElse(new BancoHoras());

        banco.setFuncionario(funcionario);
        banco.setCompetencia(competencia);
        banco.setHorasCredito(credito);
        banco.setHorasDebito(debito);
        banco.setSaldo(credito.subtract(debito));
        bancoHorasRepository.saveAndFlush(banco);
    }

    public static class RelatorioEncerramento {
        private String competencia;
        private String proximaCompetencia;
        private int funcionariosTransferidos;
        private int alertasCount;
        private java.util.List<String> alertas = new java.util.ArrayList<>();

public String getCompetencia() {
	return competencia;
}

public void setCompetencia(String competencia) {
	this.competencia = competencia;
}

public String getProximaCompetencia() {
	return proximaCompetencia;
}

public void setProximaCompetencia(String proximaCompetencia) {
	this.proximaCompetencia = proximaCompetencia;
}

public int getFuncionariosTransferidos() {
	return funcionariosTransferidos;
}

public void setFuncionariosTransferidos(int funcionariosTransferidos) {
	this.funcionariosTransferidos = funcionariosTransferidos;
}

public int getAlertasCount() {
	return alertasCount;
}

public void setAlertasCount(int alertasCount) {
	this.alertasCount = alertasCount;
}

public java.util.List<String> getAlertas() {
	return alertas;
}

public void setAlertas(java.util.List<String> alertas) {
	this.alertas = alertas;
}

    }
}
