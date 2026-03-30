package br.edu.ifrn.sinapiPRO.controller;
import javax.servlet.http.HttpServletRequest; import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper; import br.edu.ifrn.sinapiPRO.model.TipoObra;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoObraFilter; import br.edu.ifrn.sinapiPRO.service.CadastroTipoObraService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/tiposObra")
public class TiposObraController {
	@Autowired private CadastroTipoObraService service;
	@GetMapping("/novo") public ModelAndView novo(TipoObra t) { return new ModelAndView("tipoobra/CadastroTipoObra"); }
	@PostMapping({"/novo","{\\d+}"}) public ModelAndView cadastrar(@Valid TipoObra t, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(t);
		try { service.salvar(t); } catch (JaCadastradoException e) { r.rejectValue("nome", e.getMessage(), e.getMessage()); return novo(t); }
		a.addFlashAttribute("mensagem", "Tipo de obra salvo!"); return new ModelAndView("redirect:/tiposObra/novo"); }
	@GetMapping public ModelAndView pesquisar(TipoObraFilter f, BindingResult r, @PageableDefault(size=25) Pageable p, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("tipoobra/PesquisaTiposObra");
		mv.addObject("pagina", new PageWrapper<>(service.filtrar(f, p), req)); return mv; }
	@GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { ModelAndView mv = novo(service.getOne(codigo)); mv.addObject(service.getOne(codigo)); return mv; }
	@DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
		return ResponseEntity.ok().build(); }
}
