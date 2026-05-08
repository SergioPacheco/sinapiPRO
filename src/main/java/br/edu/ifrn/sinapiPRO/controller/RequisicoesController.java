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
import br.edu.ifrn.sinapiPRO.model.Requisicao;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.RequisicaoService;

@Controller
@RequestMapping("/requisicoes")
public class RequisicoesController extends AbstractObraScopedCrudListController<Requisicao> {

	private final InsumosRepository insumoRepository;

	public RequisicoesController(
			RequisicaoService service,
			ObrasRepository obraRepository,
			InsumosRepository insumoRepository) {
		super(
				service,
				"requisicao/FormRequisicao",
				"requisicao/ListaRequisicoes",
				"/requisicoes",
				"Requisição salva!",
				"descricao",
				"requisicoes",
				obraRepository,
				service::findByObra,
				requisicao -> requisicao.getObra().getCodigo());
		this.insumoRepository = insumoRepository;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView modelAndView) {
		modelAndView.addObject("insumos", insumoRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(Requisicao requisicao) {
		return abrirFormulario();
	}

	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Requisicao requisicao, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(requisicao, result, attributes);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private RequisicaoService getService() {
		return (RequisicaoService) serviceRef();
	}

	@Override
	protected Requisicao buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComItens(codigo);
	}
}
