package br.edu.ifrn.sinapiPRO.controller;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.dto.CronogramaMes;
import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.PlanejamentoItem;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;
import br.edu.ifrn.sinapiPRO.service.PlanejamentoService;

@Controller
@RequestMapping("/planejamento")
public class PlanejamentoController {

	@Autowired
	private PlanejamentoService planejamentoService;

	@Autowired
	private OrcamentoService orcamentoService;

	@GetMapping("/{codigoOrcamento}")
	public ModelAndView planejamento(@PathVariable Long codigoOrcamento) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		List<PlanejamentoItem> planejamento = planejamentoService.buscarPorOrcamento(codigoOrcamento);

		Map<Long, PlanejamentoItem> mapPlan = planejamento.stream()
				.collect(Collectors.toMap(p -> p.getItem().getCodigo(), p -> p, (a, b) -> a));

		ModelAndView mv = new ModelAndView("planejamento/Planejamento");
		mv.addObject("orcamento", orcamento);
		mv.addObject("planejamentoMap", mapPlan);
		return mv;
	}

	@PostMapping("/{codigoOrcamento}")
	public ModelAndView salvar(@PathVariable Long codigoOrcamento,
							   @RequestParam Map<String, String> params,
							   RedirectAttributes attributes) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);

		List<PlanejamentoItem> itens = orcamento.getItens().stream()
				.filter(item -> params.containsKey("inicio_" + item.getCodigo())
						&& !params.get("inicio_" + item.getCodigo()).isEmpty())
				.map(item -> {
					PlanejamentoItem pi = new PlanejamentoItem();
					pi.setOrcamento(orcamento);
					pi.setItem(item);
					pi.setDataInicio(LocalDate.parse(params.get("inicio_" + item.getCodigo())));
					pi.setDataFim(LocalDate.parse(params.get("fim_" + item.getCodigo())));
					return pi;
				}).collect(Collectors.toList());

		planejamentoService.salvar(codigoOrcamento, itens);
		attributes.addFlashAttribute("mensagem", "Planejamento salvo com sucesso!");
		return new ModelAndView("redirect:/planejamento/" + codigoOrcamento);
	}

	@GetMapping("/{codigoOrcamento}/cronograma")
	public ModelAndView cronograma(@PathVariable Long codigoOrcamento) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		List<CronogramaMes> cronograma = planejamentoService.calcularCronograma(codigoOrcamento);

		ModelAndView mv = new ModelAndView("planejamento/Cronograma");
		mv.addObject("orcamento", orcamento);
		mv.addObject("cronograma", cronograma);
		return mv;
	}
}
