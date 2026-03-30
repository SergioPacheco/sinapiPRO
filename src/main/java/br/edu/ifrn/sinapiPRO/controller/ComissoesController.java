package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Comissao;
import br.edu.ifrn.sinapiPRO.service.ComissaoService;
import br.edu.ifrn.sinapiPRO.service.VendaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/comissoes")
public class ComissoesController {

    @Autowired
    private ComissaoService service;

    @Autowired
    private VendaService vendaService;

    @GetMapping
    public ModelAndView lista() {
        ModelAndView mv = new ModelAndView("comissao/ListaComissoes");
        mv.addObject("comissoes", service.findAll());
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(Comissao comissao) {
        return form(comissao);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return form(service.getOne(codigo));
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Comissao comissao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return form(comissao);
        service.salvar(comissao);
        attributes.addFlashAttribute("mensagem", "Comissão salva!");
        return new ModelAndView("redirect:/comissoes");
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

    private ModelAndView form(Comissao comissao) {
        ModelAndView mv = new ModelAndView("comissao/FormComissao");
        mv.addObject("comissao", comissao);
        mv.addObject("vendas", vendaService.findAll());
        return mv;
    }
}
