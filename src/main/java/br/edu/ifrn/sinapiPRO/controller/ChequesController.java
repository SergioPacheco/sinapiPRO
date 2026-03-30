package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Cheque;
import br.edu.ifrn.sinapiPRO.service.ChequeService;
import br.edu.ifrn.sinapiPRO.service.ContaBancariaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/cheques")
public class ChequesController {

    @Autowired
    private ChequeService service;

    @Autowired
    private ContaBancariaService contaBancariaService;

    @GetMapping
    public ModelAndView lista() {
        ModelAndView mv = new ModelAndView("cheque/ListaCheques");
        mv.addObject("cheques", service.findAll());
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(Cheque cheque) {
        ModelAndView mv = new ModelAndView("cheque/FormCheque");
        mv.addObject("cheque", cheque);
        mv.addObject("contas", contaBancariaService.findAtivas());
        return mv;
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Cheque cheque, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return novo(cheque);
        service.salvar(cheque);
        attributes.addFlashAttribute("mensagem", "Cheque salvo!");
        return new ModelAndView("redirect:/cheques");
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
