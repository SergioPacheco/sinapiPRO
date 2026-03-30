package br.edu.ifrn.sinapiPRO.service;
import java.util.*; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.DiarioClima; import br.edu.ifrn.sinapiPRO.repository.DiarioClimasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CadastroDiarioClimaService {
	@Autowired private DiarioClimasRepository repository;
	@Transactional public DiarioClima salvar(DiarioClima e) {
		Optional<DiarioClima> ex = repository.findByNomeIgnoreCase(e.getNome());
		if (ex.isPresent() && !ex.get().getCodigo().equals(e.getCodigo())) throw new JaCadastradoException("DiarioClima já cadastrado(a)");
		return repository.saveAndFlush(e); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException ex) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso."); } }
	public List<DiarioClima> findAll() { return repository.findAll(); }
	public DiarioClima getOne(Long c) { return repository.getOne(c); }
}
