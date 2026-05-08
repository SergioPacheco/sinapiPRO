package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;
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
import br.edu.ifrn.sinapiPRO.model.DiarioObra;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.AvancoFisicoService;
import br.edu.ifrn.sinapiPRO.service.CadastroDiarioAcidenteService;
import br.edu.ifrn.sinapiPRO.service.CadastroDiarioAreaService;
import br.edu.ifrn.sinapiPRO.service.CadastroDiarioClimaService;
import br.edu.ifrn.sinapiPRO.service.DiarioObraService;

@Controller
@RequestMapping("/diarioObra")
public class DiarioObraController extends AbstractObraScopedCrudListController<DiarioObra> {

	private final ObrasRepository obraRepository;
	private final CadastroDiarioClimaService climaService;
	private final CadastroDiarioAreaService areaService;
	private final CadastroDiarioAcidenteService acidenteService;
	private final AvancoFisicoService avancoFisicoService;

	public DiarioObraController(
			DiarioObraService service,
			ObrasRepository obraRepository,
			CadastroDiarioClimaService climaService,
			CadastroDiarioAreaService areaService,
			CadastroDiarioAcidenteService acidenteService,
			AvancoFisicoService avancoFisicoService) {
		super(
				service,
				"diarioobra/FormDiarioObra",
				"diarioobra/ListaDiarioObra",
				"/diarioObra",
				"Diário salvo com sucesso!",
				"descricao",
				"diarios",
				obraRepository,
				service::findByObra,
				diarioObra -> diarioObra.getObra().getCodigo());
		this.obraRepository = obraRepository;
		this.climaService = climaService;
		this.areaService = areaService;
		this.acidenteService = acidenteService;
		this.avancoFisicoService = avancoFisicoService;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView modelAndView) {
		modelAndView.addObject("climas", climaService.findAll());
		modelAndView.addObject("areas", areaService.findAll());
		modelAndView.addObject("acidentes", acidenteService.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}

	@GetMapping("/novo")
	public ModelAndView novo(DiarioObra diarioObra) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid DiarioObra diarioObra, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(diarioObra, result, attributes);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@GetMapping("/avanco/{codigoObra}")
	public ModelAndView avanco(@PathVariable Long codigoObra,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate inicio,
			@RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate fim) {
		ModelAndView mv = new ModelAndView("diarioobra/AvancoFisico");
		mv.addObject("obra", obraRepository.findById(codigoObra).orElse(null));
		mv.addObject("servicos", avancoFisicoService.calcularAvancoPorServico(codigoObra, inicio, fim));
		mv.addObject("avancoGeral", avancoFisicoService.calcularAvancoGeral(codigoObra));
		mv.addObject("curva", avancoFisicoService.gerarCurvaAvanco(codigoObra));
		mv.addObject("inicio", inicio);
		mv.addObject("fim", fim);
		return mv;
	}

	private DiarioObraService getService() {
		return (DiarioObraService) serviceRef();
	}

	@Override
	protected DiarioObra buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComItens(codigo);
	}
}
