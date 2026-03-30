package br.edu.ifrn.sinapiPRO.controller;

import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Venda;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.UnidadeVendaService;
import br.edu.ifrn.sinapiPRO.service.VendaService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping("/vendas")
public class VendasController {

    @Autowired
    private VendaService service;

    @Autowired
    private ObrasRepository obraRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @Autowired
    private UnidadeVendaService unidadeService;

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
        ModelAndView mv = new ModelAndView("venda/ListaVendas");
        mv.addObject("obras", obraRepository.findAll());
        if (codigoObra != null) {
            mv.addObject("vendas", service.findByObra(codigoObra));
            mv.addObject("codigoObra", codigoObra);
        }
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(Venda venda) {
        return form(venda);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return form(service.buscarComParcelas(codigo));
    }

    @PostMapping({"/novo", "/{codigo}"})
    public ModelAndView salvar(@Valid Venda venda, BindingResult result, RedirectAttributes attributes) {
        if (result.hasErrors()) return form(venda);
        service.salvar(venda);
        attributes.addFlashAttribute("mensagem", "Venda registrada com sucesso!");
        return new ModelAndView("redirect:/vendas?codigoObra=" + venda.getUnidade().getObra().getCodigo());
    }

    @DeleteMapping("/{codigo}")
    public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        try {
            service.excluir(codigo);
        } catch (ImpossivelExcluirEntidadeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    private ModelAndView form(Venda venda) {
        ModelAndView mv = new ModelAndView("venda/FormVenda");
        mv.addObject("venda", venda);
        mv.addObject("unidades", unidadeService.findAll());
        mv.addObject("clientes", clienteRepository.findAll());
        return mv;
    }

    @Autowired
    private br.edu.ifrn.sinapiPRO.service.VendaParcelasService vendaParcelasService;

    @Autowired
    private br.edu.ifrn.sinapiPRO.repository.IndicesRepository indicesRepository;

    @GetMapping("/{codigo}/parcelas")
    public ModelAndView formParcelas(@PathVariable Long codigo) {
        Venda venda = service.buscarComParcelas(codigo);
        ModelAndView mv = new ModelAndView("venda/FormGerarParcelas");
        mv.addObject("venda", venda);
        mv.addObject("indices", indicesRepository.findAll());
        return mv;
    }

    @PostMapping("/{codigo}/parcelas/gerar")
    public ModelAndView gerarParcelas(@PathVariable Long codigo,
            @RequestParam java.math.BigDecimal percentualEntrada,
            @RequestParam int numeroParcelas,
            @RequestParam(defaultValue = "0") java.math.BigDecimal percentualChaves,
            @RequestParam(defaultValue = "10") int diaVencimento,
            @RequestParam @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate dataInicio,
            org.springframework.web.servlet.mvc.support.RedirectAttributes attributes) {
        try {
            java.util.List<br.edu.ifrn.sinapiPRO.model.ParcelaVenda> parcelas =
                    vendaParcelasService.gerarParcelas(codigo, percentualEntrada,
                            numeroParcelas, percentualChaves, diaVencimento, dataInicio);
            attributes.addFlashAttribute("mensagem",
                    parcelas.size() + " parcelas geradas com sucesso!");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("erro", e.getMessage());
        }
        return new ModelAndView("redirect:/vendas/" + codigo + "/parcelas");
    }

    @PostMapping("/{codigo}/parcelas/reajustar")
    public ModelAndView reajustarParcelas(@PathVariable Long codigo,
            @RequestParam Long codigoIndice,
            @RequestParam java.math.BigDecimal percentualIndice,
            org.springframework.web.servlet.mvc.support.RedirectAttributes attributes) {
        try {
            int count = vendaParcelasService.reajustarParcelas(codigo, codigoIndice, percentualIndice);
            attributes.addFlashAttribute("mensagem",
                    count + " parcelas reajustadas em " + percentualIndice + "%");
        } catch (RuntimeException e) {
            attributes.addFlashAttribute("erro", e.getMessage());
        }
        return new ModelAndView("redirect:/vendas/" + codigo + "/parcelas");
    }
}
