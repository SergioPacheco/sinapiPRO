package br.edu.ifrn.sinapiPRO.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import br.edu.ifrn.sinapiPRO.model.DocumentoGed;
import br.edu.ifrn.sinapiPRO.repository.ClientesRepository;
import br.edu.ifrn.sinapiPRO.repository.ObrasRepository;
import br.edu.ifrn.sinapiPRO.service.GedService;
import br.edu.ifrn.sinapiPRO.service.GedUploadService;

@Controller
@RequestMapping("/ged")
public class GedController {

    @Autowired
    private GedService service;

    @Autowired
    private GedUploadService uploadService;

    @Autowired
    private ObrasRepository obraRepository;

    @Autowired
    private ClientesRepository clienteRepository;

    @GetMapping
    public ModelAndView lista(@RequestParam(required = false) Long codigoObra) {
        ModelAndView mv = new ModelAndView("ged/ListaDocumentos");
        mv.addObject("obras", obraRepository.findAll());
        if (codigoObra != null) {
            mv.addObject("documentos", service.findByObra(codigoObra));
            mv.addObject("codigoObra", codigoObra);
        }
        return mv;
    }

    @GetMapping("/novo")
    public ModelAndView novo(DocumentoGed doc) {
        return form(doc);
    }

    @GetMapping("/{codigo}")
    public ModelAndView editar(@PathVariable Long codigo) {
        return form(service.getOne(codigo));
    }

    /**
     * Upload de arquivo com metadados.
     * Aceita multipart/form-data com campo 'arquivo' (MultipartFile).
     */
    @PostMapping(value = {"/novo", "/{codigo}"}, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ModelAndView upload(@Valid DocumentoGed doc,
            BindingResult result,
            @RequestParam(value = "arquivo", required = false) MultipartFile arquivo,
            RedirectAttributes attributes) {

        if (result.hasErrors()) return form(doc);

        try {
            if (arquivo != null && !arquivo.isEmpty()) {
                // Upload com arquivo
                uploadService.upload(arquivo, doc);
            } else {
                // Salva apenas metadados (sem arquivo)
                service.salvar(doc);
            }
            attributes.addFlashAttribute("mensagem", "Documento salvo com sucesso!");
        } catch (RuntimeException | IOException e) {
            attributes.addFlashAttribute("erro", e.getMessage());
            return form(doc);
        }

        return new ModelAndView("redirect:/ged");
    }

    /**
     * Download do arquivo.
     */
    @GetMapping("/{codigo}/download")
    public ResponseEntity<Resource> download(@PathVariable Long codigo) throws IOException {
        DocumentoGed doc = service.getOne(codigo);

        if (doc.getCaminho() == null) {
            return ResponseEntity.notFound().build();
        }

        Path arquivo = uploadService.getCaminhoFisico(doc);
        if (!Files.exists(arquivo)) {
            return ResponseEntity.notFound().build();
        }

        Resource resource = new FileSystemResource(arquivo);
        String contentType = doc.getTipoArquivo() != null
                ? doc.getTipoArquivo()
                : "application/octet-stream";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + doc.getNome() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }

    @DeleteMapping("/{codigo}")
    public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        try {
            uploadService.excluir(codigo);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
        return ResponseEntity.ok().build();
    }

    private ModelAndView form(DocumentoGed doc) {
        ModelAndView mv = new ModelAndView("ged/FormDocumento");
        mv.addObject("documentoGed", doc);
        mv.addObject("obras", obraRepository.findAll());
        mv.addObject("clientes", clienteRepository.findAll());
        return mv;
    }
}
