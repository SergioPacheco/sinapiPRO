package com.sinapipro.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Atendimento;
import com.sinapipro.model.Notificacao;
import com.sinapipro.repository.AtendimentosRepository;
import com.sinapipro.repository.NotificacoesRepository;

/**
 * Lógica de negócio para atendimento ao cliente com SLA e escalação.
 *
 * SLA PADRÃO (baseado em práticas de helpdesk — Zendesk/Salesforce):
 *
 * | Prioridade | Tempo de Resposta | Tempo de Resolução |
 * |------------|-------------------|-------------------|
 * | URGENTE    | 2 horas           | 8 horas           |
 * | ALTA       | 4 horas           | 24 horas          |
 * | NORMAL     | 8 horas           | 72 horas          |
 * | BAIXA      | 24 horas          | 168 horas (7 dias)|
 *
 * ESCALAÇÃO AUTOMÁTICA:
 * - Quando SLA de resolução vence → muda prioridade para nível acima
 * - BAIXA → NORMAL → ALTA → URGENTE
 * - Gera notificação automática
 *
 * FLUXO:
 * ABERTO → EM_ANDAMENTO → AGUARDANDO → ENCERRADO
 */
@Service
public class AtendimentoSlaService {

    /** SLA de resolução em horas por prioridade */
    private static final java.util.Map<String, Long> SLA_RESOLUCAO_HORAS = new java.util.LinkedHashMap<>();
    private static final java.util.Map<String, String> ESCALACAO = new java.util.LinkedHashMap<>();

    static {
        SLA_RESOLUCAO_HORAS.put("URGENTE", 8L);
        SLA_RESOLUCAO_HORAS.put("ALTA", 24L);
        SLA_RESOLUCAO_HORAS.put("NORMAL", 72L);
        SLA_RESOLUCAO_HORAS.put("BAIXA", 168L);

        ESCALACAO.put("BAIXA", "NORMAL");
        ESCALACAO.put("NORMAL", "ALTA");
        ESCALACAO.put("ALTA", "URGENTE");
        ESCALACAO.put("URGENTE", "URGENTE"); // já no máximo
    }

    private final AtendimentosRepository atendimentoRepository;
    private final NotificacoesRepository notificacaoRepository;

    public AtendimentoSlaService(
            AtendimentosRepository atendimentoRepository,
            NotificacoesRepository notificacaoRepository) {
        this.atendimentoRepository = atendimentoRepository;
        this.notificacaoRepository = notificacaoRepository;
    }

    /**
     * Calcula a data limite de resolução baseada na prioridade e data de abertura.
     */
    public LocalDateTime calcularPrazoResolucao(Atendimento atendimento) {
        String prioridade = atendimento.getPrioridade() != null
                ? atendimento.getPrioridade()
                : "NORMAL";
        long horas = SLA_RESOLUCAO_HORAS.getOrDefault(prioridade, 72L);
        return atendimento.getDataAbertura().atStartOfDay().plusHours(horas);
    }

    /**
     * Verifica se o SLA foi violado.
     */
    public boolean isSlaViolado(Atendimento atendimento) {
        if ("ENCERRADO".equals(atendimento.getSituacao())) return false;
        LocalDateTime prazo = calcularPrazoResolucao(atendimento);
        return LocalDateTime.now().isAfter(prazo);
    }

    /**
     * Calcula horas restantes para o SLA (negativo = vencido).
     */
    public long calcularHorasRestantes(Atendimento atendimento) {
        LocalDateTime prazo = calcularPrazoResolucao(atendimento);
        return ChronoUnit.HOURS.between(LocalDateTime.now(), prazo);
    }

    /**
     * Processa escalação automática de atendimentos com SLA vencido.
     *
     * Regra:
     * - Atendimentos ABERTOS ou EM_ANDAMENTO com SLA vencido
     * - Escala prioridade para o próximo nível
     * - Gera notificação automática
     *
     * @return número de atendimentos escalados
     */
    @Transactional
    public int processarEscalacoes() {
        List<Atendimento> atendimentosAbertos = atendimentoRepository.findAll().stream()
                .filter(a -> "ABERTO".equals(a.getSituacao()) || "EM_ANDAMENTO".equals(a.getSituacao()))
                .filter(this::isSlaViolado)
                .collect(Collectors.toList());

        int count = 0;
        for (Atendimento atendimento : atendimentosAbertos) {
            String prioridadeAtual = atendimento.getPrioridade() != null
                    ? atendimento.getPrioridade()
                    : "NORMAL";
            String novaPrioridade = ESCALACAO.getOrDefault(prioridadeAtual, "URGENTE");

            if (!novaPrioridade.equals(prioridadeAtual)) {
                atendimento.setPrioridade(novaPrioridade);
                atendimentoRepository.save(atendimento);

                // Gera notificação de escalação
                Notificacao notificacao = new Notificacao();
                notificacao.setTitulo("SLA Vencido — Atendimento Escalado");
                notificacao.setMensagem(String.format(
                        "Atendimento #%d '%s' escalado de %s para %s. SLA vencido há %d horas.",
                        atendimento.getCodigo(),
                        atendimento.getTitulo(),
                        prioridadeAtual,
                        novaPrioridade,
                        Math.abs(calcularHorasRestantes(atendimento))));
                notificacao.setTipo("ESCALACAO");
                notificacao.setLida(false);
                notificacao.setDataCriacao(LocalDateTime.now());
                notificacao.setAtendimento(atendimento);
                notificacaoRepository.save(notificacao);

                count++;
            }
        }

        if (count > 0) {
            atendimentoRepository.flush();
            notificacaoRepository.flush();
        }

        return count;
    }

    /**
     * Encerra um atendimento e registra o tempo de resolução.
     */
    @Transactional
    public Atendimento encerrar(Long codigoAtendimento, String observacaoEncerramento) {
        Atendimento atendimento = atendimentoRepository.findById(codigoAtendimento)
                .orElseThrow(() -> new RuntimeException("Atendimento não encontrado"));

        if ("ENCERRADO".equals(atendimento.getSituacao())) {
            throw new RuntimeException("Atendimento já está encerrado.");
        }

        atendimento.setSituacao("ENCERRADO");
        atendimento.setDataEncerramento(LocalDate.now());

        if (observacaoEncerramento != null && !observacaoEncerramento.isBlank()) {
            String obs = atendimento.getDescricao() != null ? atendimento.getDescricao() : "";
            atendimento.setDescricao(obs + "\n\n[ENCERRAMENTO] " + observacaoEncerramento);
        }

        // Gera notificação de encerramento
        Notificacao notificacao = new Notificacao();
        notificacao.setTitulo("Atendimento Encerrado");
        notificacao.setMensagem(String.format(
                "Atendimento #%d '%s' encerrado. Tempo de resolução: %d horas.",
                atendimento.getCodigo(),
                atendimento.getTitulo(),
                ChronoUnit.HOURS.between(
                        atendimento.getDataAbertura().atStartOfDay(),
                        LocalDateTime.now())));
        notificacao.setTipo("ENCERRAMENTO");
        notificacao.setLida(false);
        notificacao.setDataCriacao(LocalDateTime.now());
        notificacao.setAtendimento(atendimento);
        notificacaoRepository.saveAndFlush(notificacao);

        return atendimentoRepository.saveAndFlush(atendimento);
    }

    /**
     * Retorna atendimentos com SLA em risco (< 20% do tempo restante).
     */
    @Transactional(readOnly = true)
    public List<Atendimento> findAtendimentosEmRisco() {
        return atendimentoRepository.findAll().stream()
                .filter(a -> "ABERTO".equals(a.getSituacao()) || "EM_ANDAMENTO".equals(a.getSituacao()))
                .filter(a -> {
                    long horasRestantes = calcularHorasRestantes(a);
                    long slaTotal = SLA_RESOLUCAO_HORAS.getOrDefault(
                            a.getPrioridade() != null ? a.getPrioridade() : "NORMAL", 72L);
                    // Em risco: menos de 20% do SLA restante ou já vencido
                    return horasRestantes < (slaTotal * 0.2);
                })
                .collect(Collectors.toList());
    }

    /**
     * Retorna notificações não lidas.
     */
    @Transactional(readOnly = true)
    public List<Notificacao> findNotificacoesNaoLidas() {
        return notificacaoRepository.findByLidaFalseOrderByDataCriacaoDesc();
    }

    /**
     * Marca notificação como lida.
     */
    @Transactional
    public void marcarComoLida(Long codigoNotificacao) {
        notificacaoRepository.findById(codigoNotificacao).ifPresent(n -> {
            n.setLida(true);
            notificacaoRepository.saveAndFlush(n);
        });
    }
}
