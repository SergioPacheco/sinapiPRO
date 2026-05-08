package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudPageController;
import br.edu.ifrn.sinapiPRO.model.ComposicaoClasse;
import br.edu.ifrn.sinapiPRO.repository.filter.ComposicaoClasseFilter;
import br.edu.ifrn.sinapiPRO.service.ComposicaoClasseService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/composicaoClasses")
public class ComposicaoClassesController extends AbstractCrudPageController<ComposicaoClasse, ComposicaoClasseFilter> {

	private final ComposicaoClasseService service;

	public ComposicaoClassesController(ComposicaoClasseService service) {
		super(service, "composicaoClasse/CadastroComposicaoClasse", "composicaoClasse/PesquisaComposicaoClasses", "/composicaoClasses/nova", "Classe da Composição salva com sucesso!", "sigla");
		this.service = service;
	}

	@GetMapping("/nova")
	public ModelAndView nova(ComposicaoClasse classe) {
		return abrirFormulario();
	}

	@PostMapping({"/nova", "/{codigo}"})
	public ModelAndView cadastrar(@Valid ComposicaoClasse composicaoClasse, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(composicaoClasse, result, attributes);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid ComposicaoClasse composicaoClasse, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
		}

		try {
			composicaoClasse = service.salvar(composicaoClasse);
		} catch (JaCadastradoException exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}

		return ResponseEntity.ok(composicaoClasse);
	}

	@GetMapping
	public ModelAndView pesquisar(ComposicaoClasseFilter filtro, @PageableDefault(size = 15) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(filtro, pageable, httpServletRequest);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
