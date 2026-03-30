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
import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.SubDivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroSubDivisaoInsumoService;
import br.edu.ifrn.sinapiPRO.service.CadastroDivisaoInsumoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/subDivisoesInsumo")
public class SubDivisoesInsumoController {
	@Autowired
	private CadastroSubDivisaoInsumoService service;
	@Autowired
	private CadastroDivisaoInsumoService divisaoService;
	
	@GetMapping("/novo")
	public ModelAndView novo(SubDivisaoInsumo s) {
		ModelAndView mv = new ModelAndView("subdivisaoinsumo/CadastroSubDivisaoInsumo");
		mv.addObject("divisoes", divisaoService.findAll()); return mv;
	}
	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid SubDivisaoInsumo s, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(s);
		service.salvar(s); a.addFlashAttribute("mensagem", "Sub-divisão salva!"); return new ModelAndView("redirect:/subDivisoesInsumo/novo");
	}
	@GetMapping
	public ModelAndView pesquisar(SubDivisaoInsumoFilter f, BindingResult r, @PageableDefault(size=25) Pageable p, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("subdivisaoinsumo/PesquisaSubDivisoesInsumo");
		mv.addObject("pagina", new PageWrapper<>(service.filtrar(f, p), req)); return mv;
	}
	
	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		ModelAndView mv = novo(service.getOne(codigo)); mv.addObject(service.getOne(codigo)); return mv;
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
}
