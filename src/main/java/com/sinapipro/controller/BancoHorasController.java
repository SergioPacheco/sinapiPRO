package com.sinapipro.controller;

import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.model.MovimentacaoHora;
import com.sinapipro.service.BancoHorasService;
import com.sinapipro.service.CadastroFuncionarioService;
import com.sinapipro.service.CompetenciaService;

@Controller
@RequestMapping("/bancoHoras")
public class BancoHorasController {

    private final BancoHorasService service;
    private final CompetenciaService competenciaService;
    private final CadastroFuncionarioService funcionarioService;

    public BancoHorasController(
            BancoHorasService service,
            CompetenciaService competenciaService,
            CadastroFuncionarioService funcionarioService) {
        this.service = service;
        this.competenciaService = competenciaService;
        this.funcionarioService = funcionarioService;
    }

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoCompetencia) {
        ModelAndView mv = new ModelAndView("bancohoras/ListaBancoHoras");
        mv.addObject("competencias", competenciaService.findAll());
        if (codigoCompetencia != null) {
            mv.addObject("bancos", service.findByCompetencia(codigoCompetencia));
            mv.addObject("codigoCompetencia", codigoCompetencia);
        }
        return mv;
    }

    @GetMapping("/movimentar")
    public ModelAndView formMovimentacao(MovimentacaoHora movimentacao) {
        ModelAndView mv = new ModelAndView("bancohoras/FormMovimentacao");
        mv.addObject("movimentacaoHora", movimentacao);
        mv.addObject("funcionarios", funcionarioService.findAll());
        mv.addObject("competencias", competenciaService.findAbertas());
        return mv;
    }

    @PostMapping("/movimentar")
    public ModelAndView registrar(@Valid MovimentacaoHora movimentacao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return formMovimentacao(movimentacao);
        service.registrarMovimentacao(movimentacao);
        attributes.addFlashAttribute("mensagem", "Movimentação registrada!");
        return new ModelAndView("redirect:/bancoHoras");
    }
}
