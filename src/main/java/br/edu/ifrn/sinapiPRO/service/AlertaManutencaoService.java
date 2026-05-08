package br.edu.ifrn.sinapiPRO.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.AgendamentoManutencao;
import br.edu.ifrn.sinapiPRO.model.Veiculo;
import br.edu.ifrn.sinapiPRO.repository.AgendamentosManutencaoRepository;
import br.edu.ifrn.sinapiPRO.repository.VeiculosRepository;

/**
 * Lógica de negócio para alertas de manutenção da frota.
 *
 * REGRAS (práticas de gestão de frota):
 *
 * 1. ALERTA POR DATA:
 *    - Manutenção vencida: dataAgendamento < hoje e situação = AGENDADO
 *    - Manutenção próxima: dataAgendamento <= hoje + 7 dias
 *
 * 2. ALERTA POR KM:
 *    - Próxima manutenção preventiva por KM (ex: troca de óleo a cada 5.000 km)
 *    - Alerta quando KM atual >= KM da próxima manutenção - 500 km (margem)
 *
 * 3. INTERVALOS PADRÃO (baseados em práticas de mercado):
 *    - Troca de Óleo: 5.000 km ou 3 meses
 *    - Revisão Geral: 10.000 km ou 6 meses
 *    - Troca de Pneus: 40.000 km ou 2 anos
 *    - Freios: 20.000 km ou 1 ano
 *
 * Referência: DENATRAN, manuais dos fabricantes, práticas de gestão de frota.
 */
@Service
public class AlertaManutencaoService {

    /** Margem de alerta em KM antes do vencimento */
    private static final int MARGEM_KM = 500;

    /** Dias de antecedência para alerta por data */
    private static final int DIAS_ANTECEDENCIA = 7;

    private final VeiculosRepository veiculoRepository;
    private final AgendamentosManutencaoRepository agendamentoRepository;

    public AlertaManutencaoService(
            VeiculosRepository veiculoRepository,
            AgendamentosManutencaoRepository agendamentoRepository) {
        this.veiculoRepository = veiculoRepository;
        this.agendamentoRepository = agendamentoRepository;
    }

    /**
     * Retorna todos os alertas de manutenção ativos.
     */
    @Transactional(readOnly = true)
    public List<AlertaManutencao> gerarAlertas() {
        List<AlertaManutencao> alertas = new ArrayList<>();
        LocalDate hoje = LocalDate.now();
        LocalDate limiteAlerta = hoje.plusDays(DIAS_ANTECEDENCIA);

        List<Veiculo> veiculos = veiculoRepository.findByAtivoTrue();

        for (Veiculo veiculo : veiculos) {
            List<AgendamentoManutencao> agendamentos =
                    agendamentoRepository.findByVeiculoCodigoOrderByDataAgendamentoDesc(veiculo.getCodigo());

            for (AgendamentoManutencao ag : agendamentos) {
                if (!"AGENDADO".equals(ag.getSituacao())) continue;

                AlertaManutencao alerta = new AlertaManutencao();
                alerta.setVeiculo(veiculo);
                alerta.setAgendamento(ag);

                // Alerta por data
                if (ag.getDataAgendamento().isBefore(hoje)) {
                    long diasAtraso = ChronoUnit.DAYS.between(ag.getDataAgendamento(), hoje);
                    alerta.setTipo("VENCIDA");
                    alerta.setNivel("CRITICO");
                    alerta.setMensagem(String.format(
                            "%s — %s: VENCIDA há %d dia(s) (prevista: %s)",
                            veiculo.getPlaca(),
                            ag.getTipoManutencao(),
                            diasAtraso,
                            ag.getDataAgendamento()));
                    alertas.add(alerta);

                } else if (!ag.getDataAgendamento().isAfter(limiteAlerta)) {
                    long diasRestantes = ChronoUnit.DAYS.between(hoje, ag.getDataAgendamento());
                    alerta.setTipo("PROXIMA");
                    alerta.setNivel(diasRestantes <= 3 ? "ALTO" : "MEDIO");
                    alerta.setMensagem(String.format(
                            "%s — %s: vence em %d dia(s) (data: %s)",
                            veiculo.getPlaca(),
                            ag.getTipoManutencao(),
                            diasRestantes,
                            ag.getDataAgendamento()));
                    alertas.add(alerta);
                }
            }
        }

        // Ordena: CRITICO primeiro, depois ALTO, depois MEDIO
        alertas.sort((a, b) -> {
            int ordemA = nivelOrdem(a.getNivel());
            int ordemB = nivelOrdem(b.getNivel());
            return Integer.compare(ordemA, ordemB);
        });

        return alertas;
    }

    /**
     * Verifica se um veículo precisa de manutenção baseado no KM atual.
     * Compara com o KM da última manutenção + intervalo padrão.
     */
    @Transactional(readOnly = true)
    public List<AlertaManutencao> verificarAlertasPorKm(Long codigoVeiculo, int kmAtual) {
        List<AlertaManutencao> alertas = new ArrayList<>();
        Veiculo veiculo = veiculoRepository.findById(codigoVeiculo)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));

        List<AgendamentoManutencao> realizados = agendamentoRepository
                .findByVeiculoCodigoOrderByDataAgendamentoDesc(codigoVeiculo)
                .stream()
                .filter(a -> "REALIZADO".equals(a.getSituacao()) && a.getKmAtual() != null)
                .toList();

        // Verifica intervalos padrão por tipo de manutenção
        java.util.Map<String, Integer> intervalosKm = new java.util.LinkedHashMap<>();
        intervalosKm.put("Troca de Óleo", 5000);
        intervalosKm.put("Revisão Geral", 10000);
        intervalosKm.put("Troca de Pneus", 40000);
        intervalosKm.put("Freios", 20000);

        for (java.util.Map.Entry<String, Integer> entry : intervalosKm.entrySet()) {
            String tipo = entry.getKey();
            int intervalo = entry.getValue();

            // Busca última manutenção deste tipo
            int ultimoKm = realizados.stream()
                    .filter(a -> tipo.equals(a.getTipoManutencao()))
                    .mapToInt(AgendamentoManutencao::getKmAtual)
                    .max()
                    .orElse(0);

            int proximoKm = ultimoKm + intervalo;
            int kmParaManutencao = proximoKm - kmAtual;

            if (kmParaManutencao <= MARGEM_KM) {
                AlertaManutencao alerta = new AlertaManutencao();
                alerta.setVeiculo(veiculo);
                alerta.setTipo("KM");
                alerta.setNivel(kmParaManutencao <= 0 ? "CRITICO" : "ALTO");
                alerta.setMensagem(String.format(
                        "%s — %s: %s (KM atual: %d, próxima: %d, faltam: %d km)",
                        veiculo.getPlaca(), tipo,
                        kmParaManutencao <= 0 ? "VENCIDA" : "PRÓXIMA",
                        kmAtual, proximoKm, Math.max(0, kmParaManutencao)));
                alertas.add(alerta);
            }
        }

        return alertas;
    }

    private int nivelOrdem(String nivel) {
        if ("CRITICO".equals(nivel)) return 0;
        if ("ALTO".equals(nivel)) return 1;
        if ("MEDIO".equals(nivel)) return 2;
        return 3;
    }

    public static class AlertaManutencao {
        private Veiculo veiculo;
        private AgendamentoManutencao agendamento;
        private String tipo;   // VENCIDA, PROXIMA, KM
        private String nivel;  // CRITICO, ALTO, MEDIO
        private String mensagem;

public Veiculo getVeiculo() {
	return veiculo;
}

public void setVeiculo(Veiculo veiculo) {
	this.veiculo = veiculo;
}

public AgendamentoManutencao getAgendamento() {
	return agendamento;
}

public void setAgendamento(AgendamentoManutencao agendamento) {
	this.agendamento = agendamento;
}

public String getTipo() {
	return tipo;
}

public void setTipo(String tipo) {
	this.tipo = tipo;
}

public String getNivel() {
	return nivel;
}

public void setNivel(String nivel) {
	this.nivel = nivel;
}

public String getMensagem() {
	return mensagem;
}

public void setMensagem(String mensagem) {
	this.mensagem = mensagem;
}

    }
}
