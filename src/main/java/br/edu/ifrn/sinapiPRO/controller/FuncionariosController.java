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
import br.edu.ifrn.sinapiPRO.model.Funcionario;
import br.edu.ifrn.sinapiPRO.repository.filter.FuncionarioFilter;
import br.edu.ifrn.sinapiPRO.service.*;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller
@RequestMapping("/funcionarios")
public class FuncionariosController {
	@Autowired
	private CadastroFuncionarioService service;
	@Autowired
	private CadastroCargoService cargoService;
	@Autowired
	private CadastroFuncaoService funcaoService;
	@Autowired
	private CadastroDepartamentoService departamentoService;
	
	@GetMapping("/novo")
	public ModelAndView novo(Funcionario f) {
		ModelAndView mv = new ModelAndView("funcionario/CadastroFuncionario");
		mv.addObject("cargos", cargoService.findAll());
		mv.addObject("funcoes", funcaoService.findAll());
		mv.addObject("departamentos", departamentoService.findAll());
		return mv;
	}
	
	@PostMapping({"/novo","{\\d+}"})
	public ModelAndView cadastrar(@Valid Funcionario f, BindingResult r, RedirectAttributes a) {
		if (r.hasErrors()) return novo(f);
		service.salvar(f); a.addFlashAttribute("mensagem", "Funcionário salvo!"); return new ModelAndView("redirect:/funcionarios/novo");
	}
	@GetMapping
	public ModelAndView pesquisar(FuncionarioFilter f, BindingResult r, @PageableDefault(size=25) Pageable p, HttpServletRequest req) {
		ModelAndView mv = new ModelAndView("funcionario/PesquisaFuncionarios");
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
