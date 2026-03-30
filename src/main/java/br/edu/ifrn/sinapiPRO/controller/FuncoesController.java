package br.edu.ifrn.sinapiPRO.controller;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.Funcao;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncaoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroFuncaoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/funcoes")
public class FuncoesController {
	@Autowired
	private CadastroFuncaoService service;
	
	@GetMapping("/novo")
	public ModelAndView novo(Funcao e) {
		return new ModelAndView("funcao/CadastroFuncao");
	}

	
	@PostMapping({"/novo","{\\d+}"})
	public ModelAndView cadastrar(@Valid Funcao e, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(e);
		try {
			service.salvar(e);
		} catch (JaCadastradoException ex) {
			r.rejectValue("nome", ex.getMessage(), ex.getMessage());
			return novo(e);
		}
		a.addFlashAttribute("mensagem", "Funcao salvo(a) com sucesso!"); return new ModelAndView("redirect:/funcoes/novo");
	}
	@GetMapping
	public ModelAndView pesquisar(FuncaoFilter f, BindingResult r, @PageableDefault(size=25) Pageable p, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("funcao/PesquisaFuncoes");
		mv.addObject("pagina", new PageWrapper<>(service.filtrar(f, p), req)); return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		ModelAndView mv = novo(service.getOne(codigo));
		mv.addObject(service.getOne(codigo));
		return mv;
	}

	
	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try {
			service.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException ex) {
			return ResponseEntity.badRequest().body(ex.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
