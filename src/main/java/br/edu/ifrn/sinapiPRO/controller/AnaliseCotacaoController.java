package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.model.PedidoCompra;
import br.edu.ifrn.sinapiPRO.service.AnaliseCotacaoService;
import br.edu.ifrn.sinapiPRO.service.CotacaoService;

@Controller
@RequestMapping("/cotacoes/{codigoCotacao}/analise")
public class AnaliseCotacaoController {

    private final AnaliseCotacaoService analiseCotacaoService;
    private final CotacaoService cotacaoService;

    public AnaliseCotacaoController(AnaliseCotacaoService analiseCotacaoService, CotacaoService cotacaoService) {
        this.analiseCotacaoService = analiseCotacaoService;
        this.cotacaoService = cotacaoService;
    }

    @GetMapping
    public ModelAndView analise(@PathVariable Long codigoCotacao) {
        ModelAndView mv = new ModelAndView("cotacao/AnaliseCotacao");
        mv.addObject("cotacao", cotacaoService.buscarComItens(codigoCotacao));
        mv.addObject("analise", analiseCotacaoService.analisarCotacao(codigoCotacao));
        mv.addObject("totaisFornecedor", analiseCotacaoService.calcularTotaisPorFornecedor(codigoCotacao));
        return mv;
    }

    @PostMapping("/selecionarMenorPreco")
    public ModelAndView selecionarMenorPreco(@PathVariable Long codigoCotacao, RedirectAttributes attributes) {
        int count = analiseCotacaoService.selecionarMenorPrecoAutomatico(codigoCotacao);
        attributes.addFlashAttribute("mensagem", count + " itens selecionados automaticamente pelo menor preço.");
        return new ModelAndView("redirect:/cotacoes/" + codigoCotacao + "/analise");
    }

    @PostMapping("/gerarPedidos")
    public ModelAndView gerarPedidos(@PathVariable Long codigoCotacao, RedirectAttributes attributes) {
        try {
            List<PedidoCompra> pedidos = analiseCotacaoService.gerarPedidos(codigoCotacao);
            attributes.addFlashAttribute("mensagem",
                    pedidos.size() + " pedido(s) de compra gerado(s) com sucesso!");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("erro", e.getMessage());
        }
        return new ModelAndView("redirect:/cotacoes/" + codigoCotacao + "/analise");
    }

    @PostMapping("/selecionarResposta/{codigoResposta}")
    @ResponseBody
    public ResponseEntity<?> selecionarResposta(
            @PathVariable Long codigoCotacao,
            @PathVariable Long codigoResposta,
            @RequestParam Long codigoItem) {
        // Desmarca outras respostas do mesmo item e marca esta
        // Implementado via JS no frontend — endpoint para AJAX
        return ResponseEntity.ok().build();
    }
}
