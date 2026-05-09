package com.sinapipro.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import com.sinapipro.repository.ClientesRepository;
import com.sinapipro.repository.ComposicaoRepository;
import com.sinapipro.repository.InsumosRepository;
import com.sinapipro.repository.OrcamentosRepository;
import com.sinapipro.service.AlertaManutencaoService;
import com.sinapipro.service.AtendimentoSlaService;
import com.sinapipro.service.DespesaService;
import com.sinapipro.service.ReceitaService;

@Controller
public class DashboardController {

    private final OrcamentosRepository orcamentosRepository;
    private final InsumosRepository insumosRepository;
    private final ComposicaoRepository composicoesRepository;
    private final ClientesRepository clientesRepository;
    private final DespesaService despesaService;
    private final ReceitaService receitaService;
    private final AtendimentoSlaService atendimentoSlaService;
    private final AlertaManutencaoService alertaManutencaoService;

    public DashboardController(
            OrcamentosRepository orcamentosRepository,
            InsumosRepository insumosRepository,
            ComposicaoRepository composicoesRepository,
            ClientesRepository clientesRepository,
            DespesaService despesaService,
            ReceitaService receitaService,
            AtendimentoSlaService atendimentoSlaService,
            AlertaManutencaoService alertaManutencaoService) {
        this.orcamentosRepository = orcamentosRepository;
        this.insumosRepository = insumosRepository;
        this.composicoesRepository = composicoesRepository;
        this.clientesRepository = clientesRepository;
        this.despesaService = despesaService;
        this.receitaService = receitaService;
        this.atendimentoSlaService = atendimentoSlaService;
        this.alertaManutencaoService = alertaManutencaoService;
    }

    @GetMapping("/")
    public ModelAndView dashboard() {
        ModelAndView mv = new ModelAndView("Dashboard");

        // Métricas de orçamento
        mv.addObject("totalOrcamentos", orcamentosRepository.count());
        mv.addObject("totalInsumosSinapi", insumosRepository.countByBaseInsumoCodigo(1L));
        mv.addObject("totalInsumosPropria", insumosRepository.countByBaseInsumoCodigo(2L));
        mv.addObject("totalComposicoesSinapi", composicoesRepository.countByBaseInsumoCodigo(1L));
        mv.addObject("totalComposicoesPropria", composicoesRepository.countByBaseInsumoCodigo(2L));
        mv.addObject("totalClientes", clientesRepository.count());

        // Métricas financeiras
        long despesasAbertas = despesaService.findAbertas().size();
        long receitasAbertas = receitaService.findAbertas().size();
        mv.addObject("despesasAbertas", despesasAbertas);
        mv.addObject("receitasAbertas", receitasAbertas);

        java.math.BigDecimal totalDespesasAbertas = despesaService.findAbertas().stream()
                .map(d -> d.getValor())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        java.math.BigDecimal totalReceitasAbertas = receitaService.findAbertas().stream()
                .map(r -> r.getValor())
                .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add);
        mv.addObject("totalDespesasAbertas", totalDespesasAbertas);
        mv.addObject("totalReceitasAbertas", totalReceitasAbertas);

        // Atendimentos em risco de SLA
        java.util.List<com.sinapipro.model.Atendimento> atendimentosRisco =
                atendimentoSlaService.findAtendimentosEmRisco();
        mv.addObject("atendimentosEmRisco", atendimentosRisco.size());

        // Alertas de manutenção críticos
        long alertasCriticos = alertaManutencaoService.gerarAlertas().stream()
                .filter(a -> "CRITICO".equals(a.getNivel()))
                .count();
        mv.addObject("alertasManutencaoCriticos", alertasCriticos);

        // Notificações não lidas
        long notificacoesNaoLidas = atendimentoSlaService.findNotificacoesNaoLidas().size();
        mv.addObject("notificacoesNaoLidas", notificacoesNaoLidas);

        return mv;
    }
}
