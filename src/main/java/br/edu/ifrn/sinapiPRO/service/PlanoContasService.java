package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.PlanoContas; import br.edu.ifrn.sinapiPRO.repository.PlanoContasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class PlanoContasService {
	@Autowired private PlanoContasRepository repository;
	@Transactional public PlanoContas salvar(PlanoContas p) { return repository.saveAndFlush(p); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar. Possui sub-contas ou lançamentos."); } }
	public List<PlanoContas> findAll() { return repository.findAll(); }
	public List<PlanoContas> findRaizes() { return repository.findByPaiIsNullOrderByNumeroAsc(); }
	public PlanoContas getOne(Long c) { return repository.getOne(c); }
}
