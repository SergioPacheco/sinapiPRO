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
import com.sinapipro.model.Boleto;
import com.sinapipro.service.BoletoService;
import com.sinapipro.service.ReceitaService;

@Controller
@RequestMapping("/boletos")
public class BoletosController extends AbstractCrudListController<Boleto> {

	private final ReceitaService receitaService;

	public BoletosController(BoletoService service, ReceitaService receitaService) {
		super(service, "boleto/FormBoleto", "boleto/ListaBoletos", "/boletos", "Boleto salvo!", "descricao", "boletos");
		this.receitaService = receitaService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("receitas", receitaService.findAll());
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(Boleto boleto) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Boleto boleto, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(boleto, result, attributes);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping("/{codigo}/cancelar")
	public ModelAndView cancelar(@PathVariable Long codigo, RedirectAttributes attributes) {
		getService().cancelar(codigo);
		attributes.addFlashAttribute("mensagem", "Boleto cancelado!");
		return new ModelAndView("redirect:/boletos");
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private BoletoService getService() {
		return (BoletoService) serviceRef();
	}
}
