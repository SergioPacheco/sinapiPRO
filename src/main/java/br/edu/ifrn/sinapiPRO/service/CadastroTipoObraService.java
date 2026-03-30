package br.edu.ifrn.sinapiPRO.service;
import java.util.*; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.TipoObra; import br.edu.ifrn.sinapiPRO.repository.TiposObraRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.TipoObraFilter; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CadastroTipoObraService {
	@Autowired private TiposObraRepository repository;
	@Transactional public TipoObra salvar(TipoObra t) {
		Optional<TipoObra> e = repository.findByNomeIgnoreCase(t.getNome());
		if (e.isPresent() && !e.get().getCodigo().equals(t.getCodigo())) throw new JaCadastradoException("Tipo de obra já cadastrado");
		return repository.saveAndFlush(t); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso."); } }
	@Transactional(readOnly = true) public Page<TipoObra> filtrar(TipoObraFilter f, Pageable p) { return repository.filtrar(f, p); }
	public List<TipoObra> findAll() { return repository.findAll(); }
	public TipoObra getOne(Long c) { return repository.getOne(c); }
}
