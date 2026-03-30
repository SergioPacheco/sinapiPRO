package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.UnidadeVenda;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.SituacaoUnidadeService;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/unidadesVenda")
public class UnidadesVendaController {

    @Autowired
    private UnidadeVendaService service;

    @Autowired
    private ObrasRepository obraRepository;

    @Autowired
    private SituacaoUnidadeService situacaoService;

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
        ModelAndView mv = new ModelAndView("unidadevenda/ListaUnidades");
        mv.addObject("obras", obraRepository.findAll());
        if (codigoObra != null) {
            mv.addObject("unidades", service.findByObra(codigoObra));
            mv.addObject("codigoObra", codigoObra);
        }
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(UnidadeVenda unidade) {
        return form(unidade);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return form(service.buscarComCaracteristicas(codigo));
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid UnidadeVenda unidade, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return form(unidade);
        service.salvar(unidade);
        attributes.addFlashAttribute("mensagem", "Unidade salva com sucesso!");
        return new ModelAndView("redirect:/unidadesVenda?codigoObra=" + unidade.getObra().getCodigo());
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

    private ModelAndView form(UnidadeVenda unidade) {
        ModelAndView mv = new ModelAndView("unidadevenda/FormUnidade");
        mv.addObject("unidadeVenda", unidade);
        mv.addObject("obras", obraRepository.findAll());
        mv.addObject("situacoes", situacaoService.findAll());
        return mv;
    }
}
