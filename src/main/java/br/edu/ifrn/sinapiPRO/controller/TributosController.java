package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudPageController;
import br.edu.ifrn.sinapiPRO.model.Tributo;
import br.edu.ifrn.sinapiPRO.repository.filter.TributoFilter;
import br.edu.ifrn.sinapiPRO.service.EstadoService;
import br.edu.ifrn.sinapiPRO.service.TributoService;

@Controller
@RequestMapping("/tributos")
public class TributosController extends AbstractCrudPageController<Tributo, TributoFilter> {

	private final EstadoService estadoService;

	public TributosController(TributoService service, EstadoService estadoService) {
		super(service, "tributo/CadastroTributo", "tributo/PesquisaTributos", "/tributos/novo", "Tributo salvo com sucesso!", "descricao");
		this.estadoService = estadoService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("estados", estadoService.findAll());
	}

	@GetMapping("/novo")
	public ModelAndView novo(Tributo tributo) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid Tributo tributo, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(tributo, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(
			TributoFilter filtro,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		return processarPesquisa(filtro, pageable, request);
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
