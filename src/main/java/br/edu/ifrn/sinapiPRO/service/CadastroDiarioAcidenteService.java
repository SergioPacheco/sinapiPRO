package br.edu.ifrn.sinapiPRO.service;
import java.util.*; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.DiarioAcidente; import br.edu.ifrn.sinapiPRO.repository.DiarioAcidentesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CadastroDiarioAcidenteService {
	@Autowired private DiarioAcidentesRepository repository;
	@Transactional public DiarioAcidente salvar(DiarioAcidente e) {
		Optional<DiarioAcidente> ex = repository.findByNomeIgnoreCase(e.getNome());
		if (ex.isPresent() && !ex.get().getCodigo().equals(e.getCodigo())) throw new JaCadastradoException("DiarioAcidente já cadastrado(a)");
		return repository.saveAndFlush(e); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException ex) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso."); } }
	public List<DiarioAcidente> findAll() { return repository.findAll(); }
	public DiarioAcidente getOne(Long c) { return repository.getOne(c); }
}
