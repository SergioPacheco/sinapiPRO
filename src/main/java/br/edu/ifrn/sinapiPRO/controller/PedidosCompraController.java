package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

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
import br.edu.ifrn.sinapiPRO.model.PedidoCompra;
import br.edu.ifrn.sinapiPRO.repository.FornecedoresRepository;
import br.edu.ifrn.sinapiPRO.repository.InsumosRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.BaixaPedidoService;
import br.edu.ifrn.sinapiPRO.service.PedidoCompraService;

@Controller
@RequestMapping("/pedidosCompra")
public class PedidosCompraController extends AbstractObraScopedCrudListController<PedidoCompra> {

	private final FornecedoresRepository fornecedorRepository;
	private final InsumosRepository insumoRepository;
	private final BaixaPedidoService baixaPedidoService;

	public PedidosCompraController(
			PedidoCompraService service,
			ObrasRepository obraRepository,
			FornecedoresRepository fornecedorRepository,
			InsumosRepository insumoRepository,
			BaixaPedidoService baixaPedidoService) {
		super(
				service,
				"pedidocompra/FormPedidoCompra",
				"pedidocompra/ListaPedidosCompra",
				"/pedidosCompra",
				"Pedido salvo!",
				"descricao",
				"pedidos",
				obraRepository,
				service::findByObra,
				pedidoCompra -> pedidoCompra.getObra().getCodigo());
		this.fornecedorRepository = fornecedorRepository;
		this.insumoRepository = insumoRepository;
		this.baixaPedidoService = baixaPedidoService;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView modelAndView) {
		modelAndView.addObject("fornecedores", fornecedorRepository.findAll());
		modelAndView.addObject("insumos", insumoRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}
	
	@GetMapping("/novo")
	public ModelAndView novo(PedidoCompra pedidoCompra) {
		return abrirFormulario();
	}

	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid PedidoCompra pedidoCompra, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(pedidoCompra, result, attributes);
	}
	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@GetMapping("/{codigo}/baixa")
	public ModelAndView formBaixa(@PathVariable Long codigo) {
		ModelAndView mv = new ModelAndView("pedidocompra/FormBaixaPedido");
		mv.addObject("pedido", getService().buscarComItens(codigo));
		return mv;
	}

	@PostMapping("/{codigo}/baixa")
	public ModelAndView registrarBaixa(@PathVariable Long codigo,
			@RequestParam Map<String, String> params,
			RedirectAttributes attributes) {
		try {
			Map<Long, BigDecimal> qtds = new HashMap<>();
			params.forEach((key, value) -> {
				if (key.startsWith("qtd_") && !value.isBlank()) {
					Long itemId = Long.valueOf(key.substring(4));
					qtds.put(itemId, new BigDecimal(value));
				}
			});
			LocalDate dataRecebimento = params.containsKey("dataRecebimento") && !params.get("dataRecebimento").isBlank()
					? LocalDate.parse(params.get("dataRecebimento"))
					: LocalDate.now();
			String numeroNF = params.get("numeroNF");

			BaixaPedidoService.ResultadoBaixa resultado = baixaPedidoService.receberPedido(codigo, qtds, dataRecebimento, numeroNF);

			attributes.addFlashAttribute("mensagem",
					"Recebimento registrado! Situação: " + resultado.getSituacaoPedido()
					+ ". Estoque atualizado: " + resultado.getItensAtualizados().size() + " item(ns).");
		} catch (RuntimeException e) {
			attributes.addFlashAttribute("erro", e.getMessage());
		}
		return new ModelAndView("redirect:/pedidosCompra/" + codigo + "/baixa");
	}

	private PedidoCompraService getService() {
		return (PedidoCompraService) serviceRef();
	}

	@Override
	protected PedidoCompra buscarEntidadeParaEdicao(Long codigo) {
		return getService().buscarComItens(codigo);
	}
}
