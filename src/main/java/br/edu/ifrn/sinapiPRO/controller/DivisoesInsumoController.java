package br.edu.ifrn.sinapiPRO.controller;
import javax.servlet.http.HttpServletRequest; import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper; import br.edu.ifrn.sinapiPRO.model.DivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.DivisaoInsumoFilter; import br.edu.ifrn.sinapiPRO.service.CadastroDivisaoInsumoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/divisoesInsumo")
public class DivisoesInsumoController {
	@Autowired private CadastroDivisaoInsumoService service;
	@GetMapping("/novo") public ModelAndView novo(DivisaoInsumo d) { return new ModelAndView("divisaoinsumo/CadastroDivisaoInsumo"); }
	@PostMapping({"/novo","{\\d+}"}) public ModelAndView cadastrar(@Valid DivisaoInsumo d, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(d);
		try { service.salvar(d); } catch (JaCadastradoException e) { r.rejectValue("nome", e.getMessage(), e.getMessage()); return novo(d); }
		a.addFlashAttribute("mensagem", "Divisão de insumo salva!"); return new ModelAndView("redirect:/divisoesInsumo/novo"); }
	@GetMapping public ModelAndView pesquisar(DivisaoInsumoFilter f, BindingResult r, @PageableDefault(size=25) Pageable p, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("divisaoinsumo/PesquisaDivisoesInsumo");
		mv.addObject("pagina", new PageWrapper<>(service.filtrar(f, p), req)); return mv; }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { ModelAndView mv = novo(service.getOne(codigo)); mv.addObject(service.getOne(codigo)); return mv; }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
}
