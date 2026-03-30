package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Venda;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;
import br.edu.ifrn.sinapiPRO.service.VendaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/vendas")
public class VendasController {

    @Autowired
    private VendaService service;

    @Autowired
    private ObrasRepository obraRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @Autowired
    private UnidadeVendaService unidadeService;

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
        ModelAndView mv = new ModelAndView("venda/ListaVendas");
        mv.addObject("obras", obraRepository.findAll());
        if (codigoObra != null) {
            mv.addObject("vendas", service.findByObra(codigoObra));
            mv.addObject("codigoObra", codigoObra);
        }
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(Venda venda) {
        return form(venda);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return form(service.buscarComParcelas(codigo));
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Venda venda, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return form(venda);
        service.salvar(venda);
        attributes.addFlashAttribute("mensagem", "Venda registrada com sucesso!");
        return new ModelAndView("redirect:/vendas?codigoObra=" + venda.getUnidade().getObra().getCodigo());
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

    private ModelAndView form(Venda venda) {
        ModelAndView mv = new ModelAndView("venda/FormVenda");
        mv.addObject("venda", venda);
        mv.addObject("unidades", unidadeService.findAll());
        mv.addObject("clientes", clienteRepository.findAll());
        return mv;
    }
}
