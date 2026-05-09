package com.sinapipro.controller;

import java.math.BigDecimal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import com.sinapipro.repository.ObrasRepository;
import com.sinapipro.service.JobCostingService;

@Controller
@RequestMapping("/jobCosting")
public class JobCostingController {

    private final JobCostingService service;
    private final ObrasRepository obraRepository;

    public JobCostingController(JobCostingService service, ObrasRepository obraRepository) {
        this.service = service;
        this.obraRepository = obraRepository;
    }

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
