package com.sinapipro.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.service.ConciliacaoBancariaService;
import com.sinapipro.service.ContaBancariaService;

@Controller
@RequestMapping("/conciliacao")
public class ConciliacaoBancariaController {

    private final ConciliacaoBancariaService service;
    private final ContaBancariaService contaService;

    public ConciliacaoBancariaController(
            ConciliacaoBancariaService service,
            ContaBancariaService contaService) {
        this.service = service;
        this.contaService = contaService;
    }

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoConta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) BigDecimal saldoExtrato) {
        return criarListagem(codigoConta, inicio, fim, saldoExtrato);
    }

    private ModelAndView criarListagem(
            Long codigoConta,
            LocalDate inicio,
            LocalDate fim,
            BigDecimal saldoExtrato) {
        ModelAndView mv = new ModelAndView("conciliacao/Conciliacao");
        mv.addObject("contas", contaService.findAll());
        mv.addObject("codigoConta", codigoConta);
        mv.addObject("inicio", inicio);
        mv.addObject("fim", fim);
        mv.addObject("saldoExtrato", saldoExtrato);

        if (codigoConta != null) {
            LocalDate dataInicio = resolveDataInicio(inicio);
            LocalDate dataFim = resolveDataFim(fim);
            mv.addObject("movimentos", service.findNaoConciliados(codigoConta, dataInicio, dataFim));
            if (saldoExtrato != null) {
                mv.addObject("resumo", service.gerarResumo(codigoConta, dataFim, saldoExtrato));
            }
        }
        return mv;
    }

    private LocalDate resolveDataInicio(LocalDate inicio) {
        return inicio != null ? inicio : LocalDate.now().minusMonths(1);
    }

    private LocalDate resolveDataFim(LocalDate fim) {
        return fim != null ? fim : LocalDate.now();
    }

    @PostMapping("/{codigo}/conciliar")
    public @ResponseBody ResponseEntity<Void> conciliar(@PathVariable Long codigo) {
        service.conciliar(codigo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{codigo}/desconciliar")
    public @ResponseBody ResponseEntity<Void> desconciliar(@PathVariable Long codigo) {
        service.desconciliar(codigo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/lote")
    public ModelAndView conciliarLote(@RequestParam Long codigoConta,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            RedirectAttributes attributes) {
        int count = service.conciliarLote(codigoConta, inicio, fim);
        attributes.addFlashAttribute("mensagem", count + " movimento(s) conciliado(s).");
        return new ModelAndView("redirect:/conciliacao?codigoConta=" + codigoConta);
    }
}
