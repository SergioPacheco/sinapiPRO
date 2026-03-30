package br.edu.ifrn.sinapiPRO.controller;
import javax.servlet.http.HttpServletRequest; import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper; import br.edu.ifrn.sinapiPRO.model.Indice;
import br.edu.ifrn.sinapiPRO.repository.filter.IndiceFilter; import br.edu.ifrn.sinapiPRO.service.CadastroIndiceService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/indices")
public class IndicesController {
	@Autowired private CadastroIndiceService service;
	@GetMapping("/novo") public ModelAndView novo(Indice i) { return new ModelAndView("indice/CadastroIndice"); }
	@PostMapping({"/novo","{\\d+}"}) public ModelAndView cadastrar(@Valid Indice i, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(i);
		try { service.salvar(i); } catch (JaCadastradoException e) { r.rejectValue("nome", e.getMessage(), e.getMessage()); return novo(i); }
		a.addFlashAttribute("mensagem", "Índice salvo!"); return new ModelAndView("redirect:/indices/novo"); }
	@GetMapping public ModelAndView pesquisar(IndiceFilter f, BindingResult r, @PageableDefault(size=25) Pageable p, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("indice/PesquisaIndices");
		mv.addObject("pagina", new PageWrapper<>(service.filtrar(f, p), req)); return mv; }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { ModelAndView mv = novo(service.getOne(codigo)); mv.addObject(service.getOne(codigo)); return mv; }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
}
