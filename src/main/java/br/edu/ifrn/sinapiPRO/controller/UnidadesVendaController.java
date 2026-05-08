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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractObraScopedCrudListController;
import br.edu.ifrn.sinapiPRO.model.UnidadeVenda;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.SituacaoUnidadeService;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;

@Controller
@RequestMapping("/unidadesVenda")
public class UnidadesVendaController extends AbstractObraScopedCrudListController<UnidadeVenda> {

	private final SituacaoUnidadeService situacaoService;

	public UnidadesVendaController(
			UnidadeVendaService service,
			ObrasRepository obraRepository,
			SituacaoUnidadeService situacaoService) {
		super(
				service,
				"unidadevenda/FormUnidade",
				"unidadevenda/ListaUnidades",
				"/unidadesVenda",
				"Unidade salva com sucesso!",
				"identificacao",
				"unidades",
				obraRepository,
				service::findByObra,
				unidade -> unidade.getObra().getCodigo());
		this.situacaoService = situacaoService;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView modelAndView) {
		modelAndView.addObject("situacoes", situacaoService.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}

	@GetMapping("/novo")
	public ModelAndView novo(UnidadeVenda unidade) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid UnidadeVenda unidade, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(unidade, result, attributes);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private UnidadeVendaService getService() {
		return (UnidadeVendaService) serviceRef();
	}

	@Override
	protected UnidadeVenda buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComCaracteristicas(codigo);
	}
}
