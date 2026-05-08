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
import br.edu.ifrn.sinapiPRO.model.FormaPagamento;
import br.edu.ifrn.sinapiPRO.repository.filter.FormaPagamentoFilter;
import br.edu.ifrn.sinapiPRO.service.CadastroFormaPagamentoService;

@Controller
@RequestMapping("/formasPagamento")
public class FormasPagamentoController extends AbstractCrudPageController<FormaPagamento, FormaPagamentoFilter> {

	public FormasPagamentoController(CadastroFormaPagamentoService service) {
		super(service, "formapagamento/CadastroFormaPagamento", "formapagamento/PesquisaFormasPagamento", "/formasPagamento/novo", "Forma de pagamento salva!", "nome");
	}

	@GetMapping("/novo")
	public ModelAndView novo(FormaPagamento formaPagamento) {
		return abrirFormulario();
	}

	@PostMapping({ "/novo", "/{codigo}" })
	public ModelAndView cadastrar(@Valid FormaPagamento formaPagamento, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(formaPagamento, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(FormaPagamentoFilter filtro, @PageableDefault(size = 25) Pageable pageable, HttpServletRequest request) {
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
