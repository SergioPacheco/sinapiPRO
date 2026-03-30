package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.PrestacaoContas;
import br.edu.ifrn.sinapiPRO.service.CadastroFuncionarioService;
import br.edu.ifrn.sinapiPRO.service.CompetenciaService;
import br.edu.ifrn.sinapiPRO.service.PrestacaoContasService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/prestacaoContas")
public class PrestacaoContasController {

    @Autowired
    private PrestacaoContasService service;

    @Autowired
    private CadastroFuncionarioService funcionarioService;

    @Autowired
    private CompetenciaService competenciaService;

    @GetMapping
    public ModelAndView lista() {
        ModelAndView mv = new ModelAndView("prestacaocontas/ListaPrestacaoContas");
        mv.addObject("lancamentos", service.findPendentes());
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(PrestacaoContas prestacao) {
        return form(prestacao);
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid PrestacaoContas prestacao, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return form(prestacao);
        service.salvar(prestacao);
        attributes.addFlashAttribute("mensagem", "Lançamento salvo!");
        return new ModelAndView("redirect:/prestacaoContas");
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

    private ModelAndView form(PrestacaoContas prestacao) {
        ModelAndView mv = new ModelAndView("prestacaocontas/FormPrestacaoContas");
        mv.addObject("prestacaoContas", prestacao);
        mv.addObject("funcionarios", funcionarioService.findAll());
        mv.addObject("competencias", competenciaService.findAbertas());
        return mv;
    }
}
