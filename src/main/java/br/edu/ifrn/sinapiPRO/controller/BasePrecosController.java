package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
import br.edu.ifrn.sinapiPRO.model.BasePreco;
import br.edu.ifrn.sinapiPRO.model.Desoneracao;
import br.edu.ifrn.sinapiPRO.repository.BaseInsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.EstadosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.BasePrecoFilter;
import br.edu.ifrn.sinapiPRO.security.UsuarioSistema;
import br.edu.ifrn.sinapiPRO.service.BasePrecoService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/basePrecos")
public class BasePrecosController extends AbstractCrudPageController<BasePreco, BasePrecoFilter> {

	private final BasePrecoService service;
	private final SinapiController sinapiController;
	private final BaseInsumosRepository baseInsumosRepository;
	private final EstadosRepository estados;

	public BasePrecosController(
			BasePrecoService service,
			SinapiController sinapiController,
			BaseInsumosRepository baseInsumosRepository,
			EstadosRepository estados) {
		super(service, "basePreco/CadastroBasePreco", "basePreco/PesquisaBasePreco", "/basePrecos/nova", "Base de Preço salvo com sucesso!", "nome");
		this.service = service;
		this.sinapiController = sinapiController;
		this.baseInsumosRepository = baseInsumosRepository;
		this.estados = estados;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView mv) {
		mv.addObject("desoneracoes", Desoneracao.values());
		mv.addObject("estados", estados.findAll());
		mv.addObject("baseInsumos", baseInsumosRepository.findAll());
	}

	@GetMapping("/nova")
	public ModelAndView nova(BasePreco basePreco) {
		return abrirFormulario();
	}

	@PostMapping({"/nova", "/{codigo}"})
	public ModelAndView cadastrar(@Valid BasePreco basePreco, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(basePreco, result, attributes);
	}

	@PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
	public @ResponseBody ResponseEntity<?> salvar(@RequestBody @Valid BasePreco basePreco, BindingResult result) {
		if (result.hasErrors()) {
			return ResponseEntity.badRequest().body(result.getFieldError().getDefaultMessage());
		}

		try {
			basePreco = service.salvar(basePreco);
		} catch (JaCadastradoException exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}

		return ResponseEntity.ok(basePreco);
	}

	@GetMapping
	public ModelAndView pesquisar(BasePrecoFilter basePrecoFilter, @PageableDefault(size = 10) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(basePrecoFilter, pageable, httpServletRequest);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@GetMapping("importaInsumo/{codigo}")
	public ModelAndView importarInsumos(@PathVariable Long codigo, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		sinapiController.importaInsumos(codigo, "O", usuarioSistema);
		sinapiController.importaInsumos(codigo, "D", usuarioSistema);
		return new ModelAndView("redirect:/basePrecos/nova");
	}

	@GetMapping("importaComposicao/{codigo}")
	public ModelAndView importarComposicoes(@PathVariable Long codigo, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		sinapiController.importaComposicoes(codigo, "O", usuarioSistema);
		sinapiController.importaComposicoes(codigo, "D", usuarioSistema);
		sinapiController.novosInsumos(codigo, usuarioSistema);
		return new ModelAndView("redirect:/basePrecos/nova");
	}
}
