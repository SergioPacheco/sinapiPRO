package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.Cotacao; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.CotacaoService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/cotacoes")
public class CotacoesController {
	@Autowired private CotacaoService service;
	@Autowired private ObrasRepository obraRepository;
	@Autowired private InsumosRepository insumoRepository;
	@Autowired private FornecedoresRepository fornecedorRepository;
	@GetMapping public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
		ModelAndView mv = new ModelAndView("cotacao/ListaCotacoes");
		mv.addObject("obras", obraRepository.findAll());
		if (codigoObra != null) { mv.addObject("cotacoes", service.findByObra(codigoObra)); mv.addObject("codigoObra", codigoObra); }
		return mv; }
	@GetMapping("/novo") public ModelAndView novo(Cotacao c) { return form(c); }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { return form(service.buscarComItens(codigo)); }
	@PostMapping({"/novo","/{codigo}"}) public ModelAndView salvar(@Valid Cotacao c, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return form(c);
		service.salvar(c); a.addFlashAttribute("mensagem", "Cotação salva!");
		return new ModelAndView("redirect:/cotacoes?codigoObra=" + c.getObra().getCodigo()); }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
	private ModelAndView form(Cotacao c) {
		ModelAndView mv = new ModelAndView("cotacao/FormCotacao");
		mv.addObject("cotacao", c);
		mv.addObject("obras", obraRepository.findAll());
		mv.addObject("insumos", insumoRepository.findAll());
		mv.addObject("fornecedores", fornecedorRepository.findAll());
		return mv; }
}
