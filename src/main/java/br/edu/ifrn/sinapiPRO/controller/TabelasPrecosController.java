package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.TabelaPreco;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.TabelaPrecoService;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/tabelasPrecos")
public class TabelasPrecosController {

    @Autowired
    private TabelaPrecoService service;

    @Autowired
    private ObrasRepository obraRepository;

    @Autowired
    private UnidadeVendaService unidadeService;

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
        ModelAndView mv = new ModelAndView("tabelapreco/ListaTabelasPrecos");
        mv.addObject("obras", obraRepository.findAll());
        if (codigoObra != null) {
            mv.addObject("tabelas", service.findByObra(codigoObra));
            mv.addObject("codigoObra", codigoObra);
        }
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(TabelaPreco tabela) {
        return form(tabela);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return form(service.buscarComItens(codigo));
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid TabelaPreco tabela, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return form(tabela);
        service.salvar(tabela);
        attributes.addFlashAttribute("mensagem", "Tabela de preços salva!");
        return new ModelAndView("redirect:/tabelasPrecos?codigoObra=" + tabela.getObra().getCodigo());
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

    private ModelAndView form(TabelaPreco tabela) {
        ModelAndView mv = new ModelAndView("tabelapreco/FormTabelaPreco");
        mv.addObject("tabelaPreco", tabela);
        mv.addObject("obras", obraRepository.findAll());
        mv.addObject("unidades", unidadeService.findAll());
        return mv;
    }
}
