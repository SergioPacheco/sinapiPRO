package br.edu.ifrn.sinapiPRO.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractCrudPageController;
import br.edu.ifrn.sinapiPRO.model.Cliente;
import br.edu.ifrn.sinapiPRO.model.TipoPessoa;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.EstadosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.ClienteFilter;
import br.edu.ifrn.sinapiPRO.service.ClienteService;

@Controller
@RequestMapping("/clientes")
public class ClientesController extends AbstractCrudPageController<Cliente, ClienteFilter> {

	private final EstadosRepository estados;
	private final ClienteService service;
	private final ClientesRepository clientes;

	public ClientesController(EstadosRepository estados, ClienteService service, ClientesRepository clientes) {
		super(service, "cliente/CadastroCliente", "cliente/PesquisaClientes", "/clientes/novo", "Cliente salvo com sucesso!", "cpfOuCnpj");
		this.estados = estados;
		this.service = service;
		this.clientes = clientes;
	}

	@Override
	protected void adicionarObjetosFormulario(ModelAndView mv) {
		mv.addObject("tiposPessoa", TipoPessoa.values());
		mv.addObject("estados", estados.findAll());
	}

	@Override
	protected void adicionarObjetosPesquisa(ModelAndView mv, ClienteFilter filtro) {
		mv.addObject("tiposPessoa", TipoPessoa.values());
	}

	@GetMapping("/novo")
	public ModelAndView novo(Cliente cliente) {
		return abrirFormulario();
	}

	@PostMapping({"/novo", "/{codigo}"})
	public ModelAndView salvar(@Valid Cliente cliente, BindingResult result, RedirectAttributes attributes) {
		return processarCadastro(cliente, result, attributes);
	}

	@GetMapping
	public ModelAndView pesquisar(ClienteFilter clienteFilter, @PageableDefault(size = 3) Pageable pageable, HttpServletRequest httpServletRequest) {
		return processarPesquisa(clienteFilter, pageable, httpServletRequest);
	}

	@RequestMapping(consumes = { MediaType.APPLICATION_JSON_VALUE })
	public @ResponseBody List<Cliente> pesquisar(String nome) {
		validarTamanhoNome(nome);
		return clientes.findByNomeStartingWithIgnoreCase(nome);
	}

	private void validarTamanhoNome(String nome) {
		if (StringUtils.isEmpty(nome) || nome.length() < 3) {
			throw new IllegalArgumentException();
		}
	}

	@ExceptionHandler(IllegalArgumentException.class)
	public ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		return excluirPorCodigo(codigo);
	}

	@Override
	protected Cliente buscarEntidadeParaEdicao(Long codigo) {
		return service.buscarComCidadeEstado(codigo);
	}
}
