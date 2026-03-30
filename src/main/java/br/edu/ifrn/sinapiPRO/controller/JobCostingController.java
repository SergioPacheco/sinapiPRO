package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.JobCostingService;

@Controller
@RequestMapping("/jobCosting")
public class JobCostingController {

    @Autowired
    private JobCostingService service;

    @Autowired
    private ObrasRepository obraRepository;

    @GetMapping
    public ModelAndView selecionar() {
        ModelAndView mv = new ModelAndView("jobcosting/SelecionarObra");
        mv.addObject("obras", obraRepository.findAll());
        return mv;
    }

    @GetMapping("/{codigoObra}")
    public ModelAndView dashboard(@PathVariable Long codigoObra,
            @RequestParam(defaultValue = "0") BigDecimal percentualFisico) {
        ModelAndView mv = new ModelAndView("jobcosting/Dashboard");
        try {
            mv.addObject("relatorio", service.calcular(codigoObra, percentualFisico));
            mv.addObject("percentualFisico", percentualFisico);
            mv.addObject("codigoObra", codigoObra);
        } catch (RuntimeException e) {
            mv.addObject("erro", e.getMessage());
            mv.addObject("obras", obraRepository.findAll());
            return new ModelAndView("jobcosting/SelecionarObra", mv.getModel());
        }
        return mv;
    }
}
