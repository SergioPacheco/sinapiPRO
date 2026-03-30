package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.service.ConciliacaoBancariaService;
import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;

@Controller
@RequestMapping("/conciliacao")
public class ConciliacaoBancariaController {

    @Autowired
    private ConciliacaoBancariaService service;

    @Autowired
    private ContaBancariaService contaService;

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoConta,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim,
            @RequestParam(required = false) BigDecimal saldoExtrato) {

        ModelAndView mv = new ModelAndView("conciliacao/Conciliacao");
        mv.addObject("contas", contaService.findAll());
        mv.addObject("codigoConta", codigoConta);
        mv.addObject("inicio", inicio);
        mv.addObject("fim", fim);
        mv.addObject("saldoExtrato", saldoExtrato);

        if (codigoConta != null) {
            LocalDate dataFim = fim != null ? fim : LocalDate.now();
            mv.addObject("movimentos", service.findNaoConciliados(codigoConta,
                    inicio != null ? inicio : LocalDate.now().minusMonths(1), dataFim));
            if (saldoExtrato != null) {
                mv.addObject("resumo", service.gerarResumo(codigoConta, dataFim, saldoExtrato));
            }
        }
        return mv;
    }

    @PostMapping("/{codigo}/conciliar")
    public @ResponseBody ResponseEntity<?> conciliar(@PathVariable Long codigo) {
        service.conciliar(codigo);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{codigo}/desconciliar")
    public @ResponseBody ResponseEntity<?> desconciliar(@PathVariable Long codigo) {
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
