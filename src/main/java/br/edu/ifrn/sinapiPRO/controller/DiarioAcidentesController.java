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
import br.edu.ifrn.sinapiPRO.model.DiarioAcidente;
import br.edu.ifrn.sinapiPRO.service.CadastroDiarioAcidenteService;

@Controller
@RequestMapping("/diarioAcidentes")
public class DiarioAcidentesController extends AbstractCrudListController<DiarioAcidente> {

	public DiarioAcidentesController(CadastroDiarioAcidenteService service) {
		super(
				service,
				"diaroacidente/CadastroDiarioAcidente",
				"diaroacidente/PesquisaDiarioAcidentes",
				"/diarioAcidentes/novo",
				"DiarioAcidente salvo(a)!",
				"nome",
				"lista");
	}

	@GetMapping("/novo")
	public ModelAndView novo(DiarioAcidente diarioAcidente) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid DiarioAcidente diarioAcidente, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(diarioAcidente, result, attributes);
	}

	@GetMapping
	public ModelAndView listar() {
		return processarListagem();
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
