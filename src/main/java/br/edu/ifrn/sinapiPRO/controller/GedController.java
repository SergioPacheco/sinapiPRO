package br.edu.ifrn.sinapiPRO.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.validation.Valid;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.controller.support.AbstractObraScopedCrudListController;
import br.edu.ifrn.sinapiPRO.model.DocumentoGed;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.GedService;
import br.edu.ifrn.sinapiPRO.service.GedUploadService;

@Controller
@RequestMapping("/ged")
public class GedController extends AbstractObraScopedCrudListController<DocumentoGed> {

	private final GedService service;
	private final GedUploadService uploadService;
	private final ClientesRepository clienteRepository;

	public GedController(
			GedService service,
			GedUploadService uploadService,
			ObrasRepository obraRepository,
			ClientesRepository clienteRepository) {
		super(
				service,
				"ged/FormDocumento",
				"ged/ListaDocumentos",
				"/ged",
				"Documento salvo com sucesso!",
				"nome",
				"documentos",
				obraRepository,
				service::findByObra,
				documentoGed -> documentoGed.getObra().getCodigo());
		this.service = service;
		this.uploadService = uploadService;
		this.clienteRepository = clienteRepository;
	}

	@Override
	protected void adicionarObjetosFormularioEspecificos(ModelAndView mv) {
		mv.addObject("clientes", clienteRepository.findAll());
	}

	@GetMapping
	public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
		return processarListagemPorObra(codigoObra);
	}

	@GetMapping("/novo")
	public ModelAndView novo(DocumentoGed documentoGed) {
		return abrirFormulario();
	}

	@GetMapping("/{codigo}")
	public ModelAndView editar(@PathVariable Long codigo) {
		return carregarEdicao(codigo);
	}

	@PostMapping(value = {"/novo", "/{codigo}"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ModelAndView upload(
			@Valid DocumentoGed documentoGed,
			BindingResult result,
			@RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
			RedirectAttributes attributes) {
		if (result.hasErrors()) {
			return carregarFormulario(documentoGed);
		}

		try {
			if (arquivo != null && !arquivo.isEmpty()) {
				uploadService.upload(arquivo, documentoGed);
			} else {
				service.salvar(documentoGed);
			}
			attributes.addFlashAttribute("mensagem", "Documento salvo com sucesso!");
		} catch (RuntimeException | IOException exception) {
			attributes.addFlashAttribute("erro", exception.getMessage());
			return carregarFormulario(documentoGed);
		}

		return new ModelAndView("redirect:/ged");
	}

	@GetMapping("/{codigo}/download")
	public ResponseEntity<Resource> download(@PathVariable Long codigo) throws IOException {
		DocumentoGed documentoGed = service.buscarPorCodigo(codigo);

		if (documentoGed.getCaminho() == null) {
			return ResponseEntity.notFound().build();
		}

		Path arquivo = uploadService.getCaminhoFisico(documentoGed);
		if (!Files.exists(arquivo)) {
			return ResponseEntity.notFound().build();
		}

		Resource resource = new FileSystemResource(arquivo);
		String contentType = documentoGed.getTipoArquivo() != null
				? documentoGed.getTipoArquivo()
				: "application/octet-stream";

		return ResponseEntity.ok()
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + documentoGed.getNome() + "\"")
				.contentType(MediaType.parseMediaType(contentType))
				.body(resource);
	}

	@DeleteMapping("/{codigo}")
	public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
		try {
			uploadService.excluir(codigo);
		} catch (Exception exception) {
			return ResponseEntity.badRequest().body(exception.getMessage());
		}
		return ResponseEntity.ok().build();
	}

	private ModelAndView carregarFormulario(DocumentoGed documentoGed) {
		return abrirFormulario(documentoGed);
	}
}
