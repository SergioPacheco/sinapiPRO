package br.edu.ifrn.sinapiPRO.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudPageController;
import br.edu.ifrn.sinapiPRO.model.Usuario;
import br.edu.ifrn.sinapiPRO.repository.GruposRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.UsuarioFilter;
import br.edu.ifrn.sinapiPRO.service.StatusUsuario;
import br.edu.ifrn.sinapiPRO.service.UsuarioService;
import br.edu.ifrn.sinapiPRO.service.exception.JaCadastradoException;
import br.edu.ifrn.sinapiPRO.service.exception.SenhaObrigatoriaUsuarioException;

@Controller
@RequestMapping("/usuarios")
public class UsuariosController extends AbstractCrudPageController<Usuario, UsuarioFilter> {

	private final UsuarioService service;
	private final GruposRepository grupos;

	public UsuariosController(UsuarioService service, GruposRepository grupos) {
		super(service, "usuario/CadastroUsuario", "usuario/PesquisaUsuarios", "/usuarios/novo", "Usuário salvo com sucesso", "email");
		this.service = service;
		this.grupos = grupos;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView mv) {
		mv.addObject("grupos", grupos.findAll());
	}

	@Override
	protected void adicionarObjetosPesquisa(ModelAndView mv, UsuarioFilter filtro) {
		mv.addObject("grupos", grupos.findAll());
	}

	@GetMapping("/novo")
	public ModelAndView novo(Usuario usuario) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Usuario usuario, BindingResult result, RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return carregarFormulario(usuario);
		}

		try {
			service.salvar(usuario);
		} catch (JaCadastradoException exception) {
			result.rejectValue("email", exception.getMessage(), exception.getMessage());
			return carregarFormulario(usuario);
		} catch (SenhaObrigatoriaUsuarioException exception) {
			result.rejectValue("senha", exception.getMessage(), exception.getMessage());
			return carregarFormulario(usuario);
		}

		attributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso");
		return new ModelAndView("redirect:/usuarios/novo");
	}

	@GetMapping
	public ModelAndView pesquisar(UsuarioFilter usuarioFilter, @PageableDefault(size = 3) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(usuarioFilter, pageable, httpServletRequest);
	}

	@PutMapping("/status")
	@ResponseStatus(HttpStatus.OK)
	public void atualizarStatus(@RequestParam("codigos[]") Long[] codigos, @RequestParam("status") StatusUsuario statusUsuario) {
		service.alterarStatus(codigos, statusUsuario);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	private ModelAndView carregarFormulario(Usuario usuario) {
		return abrirFormulario(usuario);
	}

	@Override
	protected Usuario buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarComGrupos(codigo);
	}
}
