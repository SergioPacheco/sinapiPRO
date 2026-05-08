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
import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo;
import br.edu.ifrn.sinapiPRO.repository.filter.SubDivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroDivisaoInsumoService;
import br.edu.ifrn.sinapiPRO.service.CadastroSubDivisaoInsumoService;

@Controller
@RequestMapping("/subDivisoesInsumo")
public class SubDivisoesInsumoController extends AbstractCrudPageController<SubDivisaoInsumo, SubDivisaoInsumoFilter> {

	private final CadastroDivisaoInsumoService divisaoService;

	public SubDivisoesInsumoController(
			CadastroSubDivisaoInsumoService service,
			CadastroDivisaoInsumoService divisaoService) {
		super(service, "subdivisaoinsumo/CadastroSubDivisaoInsumo", "subdivisaoinsumo/PesquisaSubDivisoesInsumo", "/subDivisoesInsumo/novo", "Sub-divisão salva!", "nome");
		this.divisaoService = divisaoService;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView mv) {
		mv.addObject("divisoes", divisaoService.findAll());
	}

	@GetMapping("/novo")
	public ModelAndView novo(SubDivisaoInsumo subDivisaoInsumo) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView cadastrar(@Valid SubDivisaoInsumo subDivisaoInsumo, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(subDivisaoInsumo, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(SubDivisaoInsumoFilter filtro, @PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
		return processarPesquisa(filtro, pageable, request);
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}
}
