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
import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.ContratoService;

@Controller
@RequestMapping("/contratos")
public class ContratosController extends AbstractObraScopedCrudListController<Contrato> {

	private final ClientesRepository clienteRepository;

	public ContratosController(
			ContratoService service,
			ObrasRepository obraRepository,
			ClientesRepository clienteRepository) {
		super(
				service,
				"contrato/FormContrato",
				"contrato/ListaContratos",
				"/contratos",
				"Contrato salvo!",
				"descricao",
				"contratos",
				obraRepository,
				service::findByObra,
				contrato -> contrato.getObra().getCodigo());
		this.clienteRepository = clienteRepository;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView modelAndView) {
		modelAndView.addObject("clientes", clienteRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(Contrato contrato) {
		return abrirFormulario();
	}

	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Contrato contrato, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(contrato, result, attributes);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	private ContratoService getService() {
		return (ContratoService) serviceRef();
	}

	@Override
	protected Contrato buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComItens(codigo);
	}
}
