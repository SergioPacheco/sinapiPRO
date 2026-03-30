package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.data.domain.*;
import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.SubDivisaoInsumo; import br.edu.ifrn.sinapiPRO.repository.SubDivisoesInsumoRepository;
import br.edu.ifrn.sinapiPRO.repository.filter.SubDivisaoInsumoFilter; import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CadastroSubDivisaoInsumoService {
	@Autowired private SubDivisoesInsumoRepository repository;
	@Transactional public SubDivisaoInsumo salvar(SubDivisaoInsumo s) { return repository.saveAndFlush(s); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Já está em uso."); } }
	@Transactional(readOnly = true) public Page<SubDivisaoInsumo> filtrar(SubDivisaoInsumoFilter f, Pageable p) { return repository.filtrar(f, p); }
	public List<SubDivisaoInsumo> findAll() { return repository.findAll(); }
	public SubDivisaoInsumo getOne(Long c) { return repository.getOne(c); }
}
