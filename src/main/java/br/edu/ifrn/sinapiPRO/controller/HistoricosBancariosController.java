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
import br.edu.ifrn.sinapiPRO.model.HistoricoBancario;
import br.edu.ifrn.sinapiPRO.service.HistoricoBancarioService;

@Controller
@RequestMapping("/historicosBancarios")
public class HistoricosBancariosController extends AbstractCrudListController<HistoricoBancario> {

	public HistoricosBancariosController(HistoricoBancarioService service) {
		super(
				service,
				"historicobancario/CadastroHistoricoBancario",
				"historicobancario/ListaHistoricosBancarios",
				"/historicosBancarios",
				"Histórico salvo!",
				"descricao",
				"historicos");
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(HistoricoBancario historicoBancario) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView salvar(@Valid HistoricoBancario historicoBancario, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(historicoBancario, result, attributes);
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
