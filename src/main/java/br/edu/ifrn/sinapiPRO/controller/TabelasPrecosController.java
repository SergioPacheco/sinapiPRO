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
import br.edu.ifrn.sinapiPRO.model.TabelaPreco;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.TabelaPrecoService;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;

@Controller
@RequestMapping("/tabelasPrecos")
public class TabelasPrecosController extends AbstractObraScopedCrudListController<TabelaPreco> {

	private final UnidadeVendaService unidadeService;

	public TabelasPrecosController(
			TabelaPrecoService service,
			ObrasRepository obraRepository,
			UnidadeVendaService unidadeService) {
		super(
				service,
				"tabelapreco/FormTabelaPreco",
				"tabelapreco/ListaTabelasPrecos",
				"/tabelasPrecos",
				"Tabela de preços salva!",
				"descricao",
				"tabelas",
				obraRepository,
				service::findByObra,
				tabela -> tabela.getObra().getCodigo());
		this.unidadeService = unidadeService;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView modelAndView) {
		modelAndView.addObject("unidades", unidadeService.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}

	@GetMapping("/novo")
	public ModelAndView novo(TabelaPreco tabela) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid TabelaPreco tabela, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(tabela, result, attributes);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private TabelaPrecoService getService() {
		return (TabelaPrecoService) serviceRef();
	}

	@Override
	protected TabelaPreco buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComItens(codigo);
	}
}
