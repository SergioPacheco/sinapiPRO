package com.sinapipro.controller;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.dto.ReajustePreviewDTO;
import com.sinapipro.model.Especie;
import com.sinapipro.model.Orcamento;
import com.sinapipro.service.BasePrecoService;
import com.sinapipro.service.OrcamentoService;
import com.sinapipro.service.ReajusteService;

@Controller
@RequestMapping("/reajuste")
public class ReajusteController {

	private final ReajusteService reajusteService;
	private final OrcamentoService orcamentoService;
	private final BasePrecoService basePrecoService;

	public ReajusteController(
			ReajusteService reajusteService,
			OrcamentoService orcamentoService,
			BasePrecoService basePrecoService) {
		this.reajusteService = reajusteService;
		this.orcamentoService = orcamentoService;
		this.basePrecoService = basePrecoService;
	}

	@GetMapping("/{codigoOrcamento}")
	public ModelAndView reajuste(@PathVariable Long codigoOrcamento) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		ModelAndView mv = new ModelAndView("reajuste/Reajuste");
		mv.addObject("orcamento", orcamento);
		mv.addObject("especies", Especie.values());
		mv.addObject("basesPreco", basePrecoService.findAll());
		return mv;
	}

	@PostMapping("/{codigoOrcamento}/percentual")
	public ModelAndView aplicarPercentual(@PathVariable Long codigoOrcamento,
										  @RequestParam BigDecimal percentual,
										  @RequestParam(required = false) Especie especie,
										  RedirectAttributes attributes) {
		int count = reajusteService.reajustarPercentual(codigoOrcamento, percentual, especie);
		attributes.addFlashAttribute("mensagem", count + " itens reajustados em " + percentual + "%");
		return new ModelAndView("redirect:/reajuste/" + codigoOrcamento);
	}

	@PostMapping("/{codigoOrcamento}/valor")
	public ModelAndView aplicarValor(@PathVariable Long codigoOrcamento,
									 @RequestParam BigDecimal valor,
									 @RequestParam String codigosItens,
									 RedirectAttributes attributes) {
		List<Long> codigos = Arrays.stream(codigosItens.split(","))
				.map(String::trim).filter(s -> !s.isEmpty())
				.map(Long::valueOf).collect(Collectors.toList());
		int count = reajusteService.reajustarValor(codigoOrcamento, valor, codigos);
		attributes.addFlashAttribute("mensagem", count + " itens reajustados em R$ " + valor);
		return new ModelAndView("redirect:/reajuste/" + codigoOrcamento);
	}

	@PostMapping("/{codigoOrcamento}/aplicarSinapi")
	public ModelAndView aplicarSinapi(@PathVariable Long codigoOrcamento,
									  @RequestParam Long codigoBasePreco,
									  @RequestParam(defaultValue = "false") boolean onerado,
									  @RequestParam(required = false) Especie especie,
									  RedirectAttributes attributes) {
		int count = reajusteService.aplicarPrecoSinapi(codigoOrcamento, codigoBasePreco, onerado, especie);
		attributes.addFlashAttribute("mensagem", count + " itens atualizados com preço SINAPI");
		return new ModelAndView("redirect:/reajuste/" + codigoOrcamento);
	}

	@GetMapping("/{codigoOrcamento}/preview")
	@ResponseBody
	public List<ReajustePreviewDTO> preview(@PathVariable Long codigoOrcamento,
											@RequestParam BigDecimal percentual,
											@RequestParam(required = false) Especie especie) {
		return reajusteService.previewReajuste(codigoOrcamento, percentual, especie);
	}
}
