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
import br.edu.ifrn.sinapiPRO.model.FormaPagamento;
import br.edu.ifrn.sinapiPRO.repository.filter.FormaPagamentoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroFormaPagamentoService;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/formasPagamento")
public class FormasPagamentoController {
	@Autowired
	private CadastroFormaPagamentoService service;
	
	@GetMapping("/novo")
	public ModelAndView novo(FormaPagamento fp) {
		return new ModelAndView("formapagamento/CadastroFormaPagamento");
	}

	
	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid FormaPagamento fp, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(fp);
		try {
			service.salvar(fp);
		} catch (JaCadastradoException e) {
			r.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(fp);
		}
		a.addFlashAttribute("mensagem", "Forma de pagamento salva!"); return new ModelAndView("redirect:/formasPagamento/novo");
	}
	@GetMapping
	public ModelAndView pesquisar(FormaPagamentoFilter f, BindingResult r, @PageableDefault(size=25) Pageable p, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("formapagamento/PesquisaFormasPagamento");
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
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
