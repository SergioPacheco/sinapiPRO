package br.edu.ifrn.sinapiPRO.service;
import java.util.*; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Cargo; import br.edu.ifrn.sinapiPRO.repository.CargosRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.CargoFilter; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CadastroCargoService {
	@Autowired private CargosRepository repository;
	@Transactional public Cargo salvar(Cargo e) {
		Optional<Cargo> ex = repository.findByNomeIgnoreCase(e.getNome());
		if (ex.isPresent() && !ex.get().getCodigo().equals(e.getCodigo())) throw new JaCadastradoException("Cargo já cadastrado(a)");
		return repository.saveAndFlush(e); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException ex) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso."); } }
	@Transactional(readOnly = true) public Page<Cargo> filtrar(CargoFilter f, Pageable p) { return repository.filtrar(f, p); }
	public List<Cargo> findAll() { return repository.findAll(); }
	public Cargo getOne(Long c) { return repository.getOne(c); }
}
