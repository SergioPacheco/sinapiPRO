package com.sinapipro.controller;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.sinapipro.controller.support.AbstractObraScopedCrudListController;
import com.sinapipro.model.Cotacao;
import com.sinapipro.repository.FornecedoresRepository;
import com.sinapipro.repository.InsumosRepository;
import com.sinapipro.repository.ObrasRepository;
import com.sinapipro.service.CotacaoService;

@Controller
@RequestMapping("/cotacoes")
public class CotacoesController extends AbstractObraScopedCrudListController<Cotacao> {

	private final CotacaoService service;
	private final InsumosRepository insumoRepository;
	private final FornecedoresRepository fornecedorRepository;

	public CotacoesController(
			CotacaoService service,
			ObrasRepository obraRepository,
			InsumosRepository insumoRepository,
			FornecedoresRepository fornecedorRepository) {
		super(
				service,
				"cotacao/FormCotacao",
				"cotacao/ListaCotacoes",
				"/cotacoes",
				"Cotação salva!",
				"descricao",
				"cotacoes",
				obraRepository,
				service::findByObra,
				cotacao -> cotacao.getObra().getCodigo());
		this.service = service;
		this.insumoRepository = insumoRepository;
		this.fornecedorRepository = fornecedorRepository;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView mv) {
		mv.addObject("insumos", insumoRepository.findAll());
		mv.addObject("fornecedores", fornecedorRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}

	@GetMapping("/novo")
	public ModelAndView novo(Cotacao cotacao) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Cotacao cotacao, BindingResult result, RedirectAttributes attributes) {
		return processarCadastroPorObra(cotacao, result, attributes);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@Override
	protected Cotacao buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarComItens(codigo);
	}
}
