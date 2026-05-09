package com.sinapipro.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

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

import com.sinapipro.controller.support.AbstractCrudPageController;
import com.sinapipro.model.TipoUsuario;
import com.sinapipro.repository.filter.TipoUsuarioFilter;
import com.sinapipro.service.CadastroTipoUsuarioService;

@Controller
@RequestMapping("/tiposUsuario")
public class TipoUsuariosController extends AbstractCrudPageController<TipoUsuario, TipoUsuarioFilter> {

	public TipoUsuariosController(CadastroTipoUsuarioService service) {
		super(service, "tipousuario/CadastroTipoUsuario", "tipousuario/PesquisaTipoUsuarios", "/tiposUsuario/novo", "Tipo de usuário salvo com sucesso!", "nome");
	}

	@GetMapping("/novo")
	public ModelAndView novo(TipoUsuario tipoUsuario) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid TipoUsuario tipoUsuario, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(tipoUsuario, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(TipoUsuarioFilter filtro, @PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		return processarPesquisa(filtro, pageable, request);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
