package br.edu.ifrn.sinapiPRO.controller;

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

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudPageController;
import br.edu.ifrn.sinapiPRO.model.TipoUnidade;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoUnidadeFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroTipoUnidadeService;

@Controller
@RequestMapping("/tiposUnidade")
public class TipoUnidadesController extends AbstractCrudPageController<TipoUnidade, TipoUnidadeFilter> {

	public TipoUnidadesController(CadastroTipoUnidadeService service) {
		super(service, "tipounidade/CadastroTipoUnidade", "tipounidade/PesquisaTipoUnidades", "/tiposUnidade/novo", "Tipo de unidade salvo com sucesso!", "nome");
	}

	@GetMapping("/novo")
	public ModelAndView novo(TipoUnidade tipoUnidade) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid TipoUnidade tipoUnidade, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(tipoUnidade, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(TipoUnidadeFilter filtro, @PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
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
