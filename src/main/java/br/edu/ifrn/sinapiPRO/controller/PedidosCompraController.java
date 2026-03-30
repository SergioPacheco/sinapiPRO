package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.PedidoCompra; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.PedidoCompraService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/pedidosCompra")
public class PedidosCompraController {
	@Autowired private PedidoCompraService service;
	@Autowired private ObrasRepository obraRepository;
	@Autowired private FornecedoresRepository fornecedorRepository;
	@Autowired private InsumosRepository insumoRepository;
	@GetMapping public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		ModelAndView mv = new ModelAndView("pedidocompra/ListaPedidosCompra");
		mv.addObject("obras", obraRepository.findAll());
		if (codigoObra != null) { mv.addObject("pedidos", service.findByObra(codigoObra)); mv.addObject("codigoObra", codigoObra); }
		return mv; }
	@GetMapping("/novo") public ModelAndView novo(PedidoCompra p) { return form(p); }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { return form(service.buscarComItens(codigo)); }
	@PostMapping({"/novo","/{codigo}"}) public ModelAndView salvar(@Valid PedidoCompra p, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return form(p);
		service.salvar(p); a.addFlashAttribute("mensagem", "Pedido salvo!");
		return new ModelAndView("redirect:/pedidosCompra?codigoObra=" + p.getObra().getCodigo()); }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
	private ModelAndView form(PedidoCompra p) {
		ModelAndView mv = new ModelAndView("pedidocompra/FormPedidoCompra");
		mv.addObject("pedidoCompra", p);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("fornecedores", fornecedorRepository.findAll());
		mv.addObject("insumos", insumoRepository.findAll());
		return mv; }
}
