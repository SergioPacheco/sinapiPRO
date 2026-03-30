package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Boleto;
import br.edu.ifrn.sinapiPRO.service.BoletoService;
import br.edu.ifrn.sinapiPRO.service.ReceitaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/boletos")
public class BoletosController {

    @Autowired
    private BoletoService service;

    @Autowired
    private ReceitaService receitaService;

    @GetMapping
    public ModelAndView lista() {
        ModelAndView mv = new ModelAndView("boleto/ListaBoletos");
        mv.addObject("boletos", service.findAll());
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(Boleto boleto) {
        ModelAndView mv = new ModelAndView("boleto/FormBoleto");
        mv.addObject("boleto", boleto);
        mv.addObject("receitas", receitaService.findAll());
        return mv;
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Boleto boleto, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return novo(boleto);
        service.salvar(boleto);
        attributes.addFlashAttribute("mensagem", "Boleto salvo!");
        return new ModelAndView("redirect:/boletos");
    }

    @PostMapping("/{codigo}/cancelar")
    public ModelAndView cancelar(@PathVariable Long codigo, RedirectAttributes attributes) {
        service.cancelar(codigo);
        attributes.addFlashAttribute("mensagem", "Boleto cancelado!");
        return new ModelAndView("redirect:/boletos");
    }

    @DeleteMapping("/{codigo}")
    public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        try {
            service.excluir(codigo);
        } catch (ImpossivelExcluirEntidadeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }
}
