package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Proposta;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.service.PropostaService;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/propostas")
public class PropostasController {

    @Autowired
    private PropostaService service;

    @Autowired
    private UnidadeVendaService unidadeService;

    @Autowired
    private ClientesRepository clienteRepository;

    @GetMapping
    public ModelAndView lista() {
        ModelAndView mv = new ModelAndView("proposta/ListaPropostas");
        mv.addObject("propostas", service.findAll());
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(Proposta proposta) {
        return form(proposta);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return form(service.getOne(codigo));
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Proposta proposta, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return form(proposta);
        service.salvar(proposta);
        attributes.addFlashAttribute("mensagem", "Proposta salva!");
        return new ModelAndView("redirect:/propostas");
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

    private ModelAndView form(Proposta proposta) {
        ModelAndView mv = new ModelAndView("proposta/FormProposta");
        mv.addObject("proposta", proposta);
        mv.addObject("unidades", unidadeService.findAll());
        mv.addObject("clientes", clienteRepository.findAll());
        return mv;
    }
}
