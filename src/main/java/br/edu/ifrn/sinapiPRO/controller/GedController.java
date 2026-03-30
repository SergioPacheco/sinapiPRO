package br.edu.ifrn.sinapiPRO.controller;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller; import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*; import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import br.edu.ifrn.sinapiPRO.model.DocumentoGed; import br.edu.ifrn.sinapiPRO.repository.*;
import br.edu.ifrn.sinapiPRO.service.GedService; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Controller @RequestMapping("/ged")
public class GedController {
    @Autowired private GedService service;
    @Autowired private ObrasRepository obraRepository;
    @Autowired private ClientesRepository clienteRepository;
    @GetMapping public ModelAndView lista(@RequestParam(required=false) Long codigoObra) {
        ModelAndView mv = new ModelAndView("ged/ListaDocumentos");
        mv.addObject("obras", obraRepository.findAll());
        if (codigoObra != null) { mv.addObject("documentos", service.findByObra(codigoObra)); mv.addObject("codigoObra", codigoObra); }
        return mv; }
    @GetMapping("/novo") public ModelAndView novo(DocumentoGed doc) { return form(doc); }
    @GetMapping("/{codigo}") public ModelAndView editar(@PathVariable Long codigo) { return form(service.getOne(codigo)); }
    @PostMapping({"/novo","/{codigo}"}) public ModelAndView salvar(@Valid DocumentoGed doc, BindingResult r, RedirectAttributes attrs) {
        if (r.hasErrors()) return form(doc);
        service.salvar(doc); attrs.addFlashAttribute("mensagem", "Documento salvo!"); return new ModelAndView("redirect:/ged"); }
    @DeleteMapping("/{codigo}") public @ResponseBody ResponseEntity<?> excluir(@PathVariable Long codigo) {
        try { service.excluir(codigo); } catch (ImpossivelExcluirEntidadeException e) { return ResponseEntity.badRequest().body(e.getMessage()); }
        return ResponseEntity.ok().build(); }
    private ModelAndView form(DocumentoGed doc) {
        ModelAndView mv = new ModelAndView("ged/FormDocumento");
        mv.addObject("documentoGed", doc); mv.addObject("obras", obraRepository.findAll()); mv.addObject("clientes", clienteRepository.findAll()); return mv; }
}
