package br.edu.ifrn.sinapiPRO.service;
import java.util.*; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.DiarioArea; import br.edu.ifrn.sinapiPRO.repository.DiarioAreasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CadastroDiarioAreaService {
	@Autowired private DiarioAreasRepository repository;
	@Transactional public DiarioArea salvar(DiarioArea e) {
		Optional<DiarioArea> ex = repository.findByNomeIgnoreCase(e.getNome());
		if (ex.isPresent() && !ex.get().getCodigo().equals(e.getCodigo())) throw new JaCadastradoException("DiarioArea já cadastrado(a)");
		return repository.saveAndFlush(e); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException ex) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso."); } }
	public List<DiarioArea> findAll() { return repository.findAll(); }
	public DiarioArea getOne(Long c) { return repository.getOne(c); }
}
