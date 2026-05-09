package com.sinapipro.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
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

import com.sinapipro.controller.support.AbstractObraScopedCrudListController;
import com.sinapipro.model.ParcelaVenda;
import com.sinapipro.model.Venda;
import com.sinapipro.repository.ClientesRepository;
import com.sinapipro.repository.IndicesRepository;
import com.sinapipro.repository.ObrasRepository;
import com.sinapipro.service.UnidadeVendaService;
import com.sinapipro.service.VendaParcelasService;
import com.sinapipro.service.VendaService;

@Controller
@RequestMapping("/vendas")
public class VendasController extends AbstractObraScopedCrudListController<Venda> {

	private final VendaService service;
	private final ClientesRepository clienteRepository;
	private final UnidadeVendaService unidadeService;
	private final VendaParcelasService vendaParcelasService;
	private final IndicesRepository indicesRepository;

	public VendasController(
			VendaService service,
			ObrasRepository obraRepository,
			ClientesRepository clienteRepository,
			UnidadeVendaService unidadeService,
			VendaParcelasService vendaParcelasService,
			IndicesRepository indicesRepository) {
		super(
				service,
				"venda/FormVenda",
				"venda/ListaVendas",
				"/vendas",
				"Venda registrada com sucesso!",
				"descricao",
				"vendas",
				obraRepository,
				service::findByObra,
				venda -> venda.getUnidade().getObra().getCodigo());
		this.service = service;
		this.clienteRepository = clienteRepository;
		this.unidadeService = unidadeService;
		this.vendaParcelasService = vendaParcelasService;
		this.indicesRepository = indicesRepository;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView mv) {
		mv.addObject("unidades", unidadeService.findAll());
		mv.addObject("clientes", clienteRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}

	@GetMapping("/novo")
	public ModelAndView novo(Venda venda) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Venda venda, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(venda, result, attributes);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@GetMapping("/{codigo}/parcelas")
	public ModelAndView formParcelas(@PathVariable Long codigo) {
		Venda venda = service.buscarComParcelas(codigo);
		ModelAndView mv = new ModelAndView("venda/FormGerarParcelas");
		mv.addObject("venda", venda);
		mv.addObject("indices", indicesRepository.findAll());
		return mv;
	}

	@PostMapping("/{codigo}/parcelas/gerar")
	public ModelAndView gerarParcelas(
			@PathVariable Long codigo,
			@RequestParam BigDecimal percentualEntrada,
			@RequestParam int numeroParcelas,
			@RequestParam(defaultValue = "0") BigDecimal percentualChaves,
			@RequestParam(defaultValue = "10") int diaVencimento,
			@RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) LocalDate dataInicio,
			RedirectAttributes attributes) {
		try {
			List<ParcelaVenda> parcelas = vendaParcelasService.gerarParcelas(
					codigo,
					percentualEntrada,
					numeroParcelas,
					percentualChaves,
					diaVencimento,
					dataInicio);
			attributes.addFlashAttribute("mensagem", parcelas.size() + " parcelas geradas com sucesso!");
		} catch (RuntimeException exception) {
			attributes.addFlashAttribute("erro", exception.getMessage());
		}
		return new ModelAndView("redirect:/vendas/" + codigo + "/parcelas");
	}

	@PostMapping("/{codigo}/parcelas/reajustar")
	public ModelAndView reajustarParcelas(
			@PathVariable Long codigo,
			@RequestParam Long codigoIndice,
			@RequestParam BigDecimal percentualIndice,
			RedirectAttributes attributes) {
		try {
			int count = vendaParcelasService.reajustarParcelas(codigo, codigoIndice, percentualIndice);
			attributes.addFlashAttribute("mensagem", count + " parcelas reajustadas em " + percentualIndice + "%");
		} catch (RuntimeException exception) {
			attributes.addFlashAttribute("erro", exception.getMessage());
		}
		return new ModelAndView("redirect:/vendas/" + codigo + "/parcelas");
	}

	@Override
	protected Venda buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarComParcelas(codigo);
	}
}
