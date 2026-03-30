package br.edu.ifrn.sinapiPRO.service;
import java.time.LocalDateTime; import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.DocumentoGed; import br.edu.ifrn.sinapiPRO.repository.DocumentosGedRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;
@Service public class GedService {
    @Autowired private DocumentosGedRepository repository;
    @Transactional public DocumentoGed salvar(DocumentoGed doc) {
        if (doc.getDataUpload() == null) doc.setDataUpload(LocalDateTime.now());
        return repository.saveAndFlush(doc); }
    @Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar o documento."); } }
    @Transactional(readOnly = true) public List<DocumentoGed> findByObra(Long codigoObra) { return repository.findByObraCodigoOrderByDataUploadDesc(codigoObra); }
    public List<DocumentoGed> findAll() { return repository.findAll(); }
    public DocumentoGed getOne(Long c) { return repository.getOne(c); }
}
