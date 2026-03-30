package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.page.PageWrapper;
import br.edu.ifrn.sinapiPRO.model.TipoUsuario;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUsuarioFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroTipoUsuarioService;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;

@Controller
@RequestMapping("/tiposUsuario")
public class TipoUsuariosController {

	@Autowired
	private CadastroTipoUsuarioService service;

	@GetMapping("/novo")
	public ModelAndView novo(TipoUsuario tipoUsuario) {
		return new ModelAndView("tipousuario/CadastroTipoUsuario");
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid TipoUsuario tipoUsuario, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return novo(tipoUsuario);
		}
		try {
			service.salvar(tipoUsuario);
		} catch (JaCadastradoException e) {
			result.rejectValue("nome", e.getMessage(), e.getMessage());
			return novo(tipoUsuario);
		}
		attributes.addFlashAttribute("mensagem", "Tipo de usuário salvo com sucesso!");
		return new ModelAndView("redirect:/tiposUsuario/novo");
	}

	@GetMapping
	public ModelAndView pesquisar(TipoUsuarioFilter filtro, BindingResult result,
			@PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		ModelAndView mv = new ModelAndView("tipousuario/PesquisaTipoUsuarios");
		PageWrapper<TipoUsuario> paginaWrapper = new PageWrapper<>(service.filtrar(filtro, pageable), request);
		mv.addObject("pagina", paginaWrapper);
		return mv;
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		TipoUsuario tipoUsuario = service.getOne(codigo);
		ModelAndView mv = novo(tipoUsuario);
		mv.addObject(tipoUsuario);
		return mv;
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			service.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
}
