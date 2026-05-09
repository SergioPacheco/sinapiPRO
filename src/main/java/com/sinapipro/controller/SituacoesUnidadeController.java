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
import com.sinapipro.model.SituacaoUnidade;
import com.sinapipro.service.SituacaoUnidadeService;

@Controller
@RequestMapping("/situacoesUnidade")
public class SituacoesUnidadeController extends AbstractCrudListController<SituacaoUnidade> {

	public SituacoesUnidadeController(SituacaoUnidadeService service) {
		super(
				service,
				"situacaounidade/CadastroSituacao",
				"situacaounidade/ListaSituacoes",
				"/situacoesUnidade",
				"Situação salva!",
				"nome",
				"situacoes");
	}

	@GetMapping
	public ModelAndView lista() {
		return processarListagem();
	}

	@GetMapping("/novo")
	public ModelAndView novo(SituacaoUnidade situacao) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView salvar(@Valid SituacaoUnidade situacao, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(situacao, result, attributes);
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
