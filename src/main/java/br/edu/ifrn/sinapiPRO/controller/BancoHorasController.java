package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.MovimentacaoHora;
import br.edu.ifrn.sinapiPRO.service.BancoHorasService;
import br.edu.ifrn.sinapiPRO.service.CadastroFuncionarioService;
import br.edu.ifrn.sinapiPRO.service.CompetenciaService;

@Controller
@RequestMapping("/bancoHoras")
public class BancoHorasController {

    @Autowired
    private BancoHorasService service;

    @Autowired
    private CompetenciaService competenciaService;

    @Autowired
    private CadastroFuncionarioService funcionarioService;

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
