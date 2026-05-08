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
import br.edu.ifrn.sinapiPRO.model.Funcionario;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncionarioFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroCargoService;
import br.edu.ifrn.sinapiPRO.service.CadastroDepartamentoService;
import br.edu.ifrn.sinapiPRO.service.CadastroFuncionarioService;
import br.edu.ifrn.sinapiPRO.service.CadastroFuncaoService;

@Controller
@RequestMapping("/funcionarios")
public class FuncionariosController extends AbstractCrudPageController<Funcionario, FuncionarioFilter> {

	private final CadastroCargoService cargoService;
	private final CadastroFuncaoService funcaoService;
	private final CadastroDepartamentoService departamentoService;

	public FuncionariosController(
			CadastroFuncionarioService service,
			CadastroCargoService cargoService,
			CadastroFuncaoService funcaoService,
			CadastroDepartamentoService departamentoService) {
		super(service, "funcionario/CadastroFuncionario", "funcionario/PesquisaFuncionarios", "/funcionarios/novo", "Funcionário salvo!", "nome");
		this.cargoService = cargoService;
		this.funcaoService = funcaoService;
		this.departamentoService = departamentoService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("cargos", cargoService.findAll());
		modelAndView.addObject("funcoes", funcaoService.findAll());
		modelAndView.addObject("departamentos", departamentoService.findAll());
	}

	@GetMapping("/novo")
	public ModelAndView novo(Funcionario funcionario) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid Funcionario funcionario, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(funcionario, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(FuncionarioFilter filtro, @PageableDefault(size=25) Pageable pageable, HttpServletRequest request) {
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
