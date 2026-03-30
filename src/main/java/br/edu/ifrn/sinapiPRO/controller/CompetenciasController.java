package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Competencia;
import br.edu.ifrn.sinapiPRO.service.CompetenciaService;

@Controller
@RequestMapping("/competencias")
public class CompetenciasController {

    @Autowired
    private CompetenciaService service;

    @GetMapping
    public ModelAndView lista() {
        ModelAndView mv = new ModelAndView("competencia/ListaCompetencias");
        mv.addObject("competencias", service.findAll());
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(Competencia competencia) {
        return new ModelAndView("competencia/CadastroCompetencia");
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Competencia competencia, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return novo(competencia);
        service.salvar(competencia);
        attributes.addFlashAttribute("mensagem", "Competência salva!");
        return new ModelAndView("redirect:/competencias");
    }

    @PostMapping("/{codigo}/encerrar")
    public ModelAndView encerrar(@PathVariable Long codigo, RedirectAttributes attributes) {
        service.encerrar(codigo);
        attributes.addFlashAttribute("mensagem", "Competência encerrada!");
        return new ModelAndView("redirect:/competencias");
    }
}
