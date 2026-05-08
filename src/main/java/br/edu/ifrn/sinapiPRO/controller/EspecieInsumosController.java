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
import br.edu.ifrn.sinapiPRO.model.EspecieInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.EspecieInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroEspecieInsumoService;

@Controller
@RequestMapping("/especiesInsumo")
public class EspecieInsumosController extends AbstractCrudPageController<EspecieInsumo, EspecieInsumoFilter> {

	public EspecieInsumosController(CadastroEspecieInsumoService service) {
		super(service, "especieinsumo/CadastroEspecieInsumo", "especieinsumo/PesquisaEspecieInsumos", "/especiesInsumo/novo", "Espécie de insumo salva com sucesso!", "nome");
	}

	@GetMapping("/novo")
	public ModelAndView novo(EspecieInsumo especieInsumo) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid EspecieInsumo especieInsumo, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(especieInsumo, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(EspecieInsumoFilter filtro, @PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
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
