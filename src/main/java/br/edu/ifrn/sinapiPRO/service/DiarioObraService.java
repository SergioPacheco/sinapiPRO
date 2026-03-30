package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.DiarioObra; import br.edu.ifrn.sinapiPRO.repository.DiarioObraRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class DiarioObraService {
	@Autowired private DiarioObraRepository repository;
	@Transactional public DiarioObra salvar(DiarioObra d) { return repository.saveAndFlush(d); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar o diário."); } }
	@Transactional(readOnly = true) public List<DiarioObra> findByObra(Long codigoObra) { return repository.findByObraCodigoOrderByDataDesc(codigoObra); }
	@Transactional(readOnly = true) public DiarioObra buscarComItens(Long codigo) { return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Diário não encontrado")); }
}
