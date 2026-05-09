package com.sinapipro.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractCrudListController;
import com.sinapipro.model.Comissao;
import com.sinapipro.service.ComissaoService;
import com.sinapipro.service.VendaService;

@Controller
@RequestMapping("/comissoes")
public class ComissoesController extends AbstractCrudListController<Comissao> {

    private final VendaService vendaService;

    public ComissoesController(ComissaoService service, VendaService vendaService) {
        super(service, "comissao/FormComissao", "comissao/ListaComissoes", "/comissoes", "Comissão salva!", "nomeCorretor", "comissoes");
        this.vendaService = vendaService;
    }

    @Override
    protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
        modelAndView.addObject("vendas", vendaService.findAll());
    }

    @GetMapping
    public ModelAndView lista() {
        return processarListagem();
    }

    @GetMapping("/novo")
    public ModelAndView novo(Comissao comissao) {
        return abrirFormulario();
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return carregarEdicao(codigo);
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Comissao comissao, BindingResult result, RedirectAttributes attributes) {
        return processarCadastro(comissao, result, attributes);
    }

    @DeleteMapping("/{codigo}")
    public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        return excluirPorCodigo(codigo);
    }
}
