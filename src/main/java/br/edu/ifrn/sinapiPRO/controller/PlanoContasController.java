package br.edu.ifrn.sinapiPRO.controller;

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

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudListController;
import br.edu.ifrn.sinapiPRO.model.PlanoContas;
import br.edu.ifrn.sinapiPRO.service.PlanoContasService;

@Controller
@RequestMapping("/planoContas")
public class PlanoContasController extends AbstractCrudListController<PlanoContas> {

	private final PlanoContasService service;

	public PlanoContasController(PlanoContasService service) {
		super(service, "planocontas/CadastroPlanoContas", "planocontas/ListaPlanoContas", "/planoContas", "Conta salva!", "numero", "contas");
		this.service = service;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("pais", service.findAll());
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(PlanoContas planoContas) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid PlanoContas planoContas, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(planoContas, result, attributes);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
