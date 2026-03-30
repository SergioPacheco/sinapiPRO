package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult; import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView; import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.*; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.EstoqueService;
@Controller @RequestMapping("/estoque")
public class EstoqueController {
	@Autowired private EstoqueService service;
	@Autowired private ObrasRepository obraRepository;
	@Autowired private InsumosRepository insumoRepository;
	@GetMapping public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		ModelAndView mv = new ModelAndView("estoque/ListaEstoque");
		mv.addObject("obras", obraRepository.findAll());
		if (codigoObra != null) { mv.addObject("estoques", service.findByObra(codigoObra)); mv.addObject("codigoObra", codigoObra); }
		return mv; }
	@GetMapping("/novo") public ModelAndView novo(Estoque e) {
		ModelAndView mv = new ModelAndView("estoque/FormEstoque");
		mv.addObject("estoque", e);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("insumos", insumoRepository.findAll());
		return mv; }
	@PostMapping("/novo") public ModelAndView salvar(@Valid Estoque e, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(e);
		service.salvar(e); a.addFlashAttribute("mensagem", "Estoque salvo!");
		return new ModelAndView("redirect:/estoque?codigoObra=" + e.getObra().getCodigo()); }
	@GetMapping("/{codigo}") public ModelAndView detalhe(@PathVariable Long codigo) {
		Estoque e = service.buscarComMovimentos(codigo);
		ModelAndView mv = new ModelAndView("estoque/DetalheEstoque");
		mv.addObject("estoque", e);
		mv.addObject("novoMovimento", new MovimentoEstoque());
		return mv; }
	@PostMapping("/{codigo}/movimentar") public ModelAndView movimentar(@PathVariable Long codigo,
			@Valid MovimentoEstoque movimento, BindingResult r, RedirectAttributes a) {
		service.movimentar(codigo, movimento);
		a.addFlashAttribute("mensagem", "Movimento registrado!");
		return new ModelAndView("redirect:/estoque/" + codigo); }
}
