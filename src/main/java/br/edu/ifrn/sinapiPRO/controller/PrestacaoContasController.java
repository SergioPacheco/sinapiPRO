package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudListController;
import br.edu.ifrn.sinapiPRO.model.PrestacaoContas;
import br.edu.ifrn.sinapiPRO.service.CadastroFuncionarioService;
import br.edu.ifrn.sinapiPRO.service.CompetenciaService;
import br.edu.ifrn.sinapiPRO.service.PrestacaoContasService;

@Controller
@RequestMapping("/prestacaoContas")
public class PrestacaoContasController extends AbstractCrudListController<PrestacaoContas> {

	private final CadastroFuncionarioService funcionarioService;
	private final CompetenciaService competenciaService;

	public PrestacaoContasController(
			PrestacaoContasService service,
			CadastroFuncionarioService funcionarioService,
			CompetenciaService competenciaService) {
		super(service, "prestacaocontas/FormPrestacaoContas", "prestacaocontas/ListaPrestacaoContas", "/prestacaoContas", "Lançamento salvo!", "descricao", "lancamentos");
		this.funcionarioService = funcionarioService;
		this.competenciaService = competenciaService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView modelAndView) {
		modelAndView.addObject("funcionarios", funcionarioService.findAll());
		modelAndView.addObject("competencias", competenciaService.findAbertas());
	}

	@GetMapping
	public ModelAndView lista() {
		ModelAndView mv = new ModelAndView("prestacaocontas/ListaPrestacaoContas");
		mv.addObject("lancamentos", ((PrestacaoContasService) serviceRef()).findPendentes());
		return mv;
	}

	@GetMapping("/novo")
	public ModelAndView novo(PrestacaoContas prestacao) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid PrestacaoContas prestacao, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(prestacao, result, attributes);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
