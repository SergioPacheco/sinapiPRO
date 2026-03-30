package br.edu.ifrn.sinapiPRO.service;
import java.util.*; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.DivisaoInsumo; import br.edu.ifrn.sinapiPRO.repository.DivisoesInsumoRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.DivisaoInsumoFilter;
import br.edu.ifrn.sinapiPRO.service.exception.*; 
@Service public class CadastroDivisaoInsumoService {
	@Autowired private DivisoesInsumoRepository repository;
	@Transactional public DivisaoInsumo salvar(DivisaoInsumo d) {
		Optional<DivisaoInsumo> e = repository.findByNomeIgnoreCase(d.getNome());
		if (e.isPresent() && !e.get().getCodigo().equals(d.getCodigo())) throw new JaCadastradoException("Divisão já cadastrada");
		return repository.saveAndFlush(d); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso."); } }
	@Transactional(readOnly = true) public Page<DivisaoInsumo> filtrar(DivisaoInsumoFilter f, Pageable p) { return repository.filtrar(f, p); }
	public List<DivisaoInsumo> findAll() { return repository.findAll(); }
	public DivisaoInsumo getOne(Long c) { return repository.getOne(c); }
}
