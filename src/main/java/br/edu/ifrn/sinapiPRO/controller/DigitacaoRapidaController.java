package br.edu.ifrn.sinapiPRO.controller;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.model.Item;
import br.edu.ifrn.sinapiPRO.model.Orcamento;
import br.edu.ifrn.sinapiPRO.model.Tipo;
import br.edu.ifrn.sinapiPRO.service.ItemService;
import br.edu.ifrn.sinapiPRO.service.OrcamentoService;

@Controller
@RequestMapping("/digitacaoRapida")
public class DigitacaoRapidaController {

	@Autowired
	private OrcamentoService orcamentoService;

	@Autowired
	private ItemService itemService;

	@GetMapping("/{codigoOrcamento}")
	public ModelAndView tela(@PathVariable Long codigoOrcamento) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);
		ModelAndView mv = new ModelAndView("orcamento/DigitacaoRapida");
		mv.addObject("orcamento", orcamento);
		return mv;
	}

	@PostMapping("/{codigoOrcamento}")
	public ModelAndView adicionar(@PathVariable Long codigoOrcamento,
								  @RequestParam String descricao,
								  @RequestParam BigDecimal quantidade,
								  @RequestParam BigDecimal valorUnitario,
								  @RequestParam(required = false) String unidade,
								  RedirectAttributes attributes) {
		Orcamento orcamento = orcamentoService.buscarComItens(codigoOrcamento);

		Item item = new Item();
		item.setOrcamento(orcamento);
		item.setDescricao(descricao);
		item.setQuantidade(quantidade);
		item.setValorUnitario(valorUnitario);
		item.setUnidade(unidade);
		item.setTipo(Tipo.INSUMO);
		itemService.salvar(item);

		attributes.addFlashAttribute("mensagem", "Item adicionado: " + descricao);
		return new ModelAndView("redirect:/digitacaoRapida/" + codigoOrcamento);
	}
}
