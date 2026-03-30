package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.SituacaoUnidade;
import br.edu.ifrn.sinapiPRO.service.SituacaoUnidadeService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/situacoesUnidade")
public class SituacoesUnidadeController {

    @Autowired
    private SituacaoUnidadeService service;

    @GetMapping
    public ModelAndView lista() {
        ModelAndView mv = new ModelAndView("situacaounidade/ListaSituacoes");
        mv.addObject("situacoes", service.findAll());
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(SituacaoUnidade situacao) {
        return new ModelAndView("situacaounidade/CadastroSituacao");
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid SituacaoUnidade situacao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return novo(situacao);
        service.salvar(situacao);
        attributes.addFlashAttribute("mensagem", "Situação salva!");
        return new ModelAndView("redirect:/situacoesUnidade");
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        ModelAndView mv = novo(service.getOne(codigo));
        mv.addObject(service.getOne(codigo));
        return mv;
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
