package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Cotacao; import br.edu.ifrn.sinapiPRO.repository.CotacoesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class CotacaoService {
	@Autowired private CotacoesRepository repository;
	@Transactional public Cotacao salvar(Cotacao c) {
		c.getItens().forEach(i -> i.setCotacao(c));
		c.getFornecedores().forEach(f -> f.setCotacao(c));
		return repository.saveAndFlush(c); }
	@Transactional public void excluir(Long codigo) { try { repository.deleteById(codigo); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar a cotação."); } }
	@Transactional(readOnly = true) public List<Cotacao> findByObra(Long codigoObra) { return repository.findByObraCodigoOrderByDataCotacaoDesc(codigoObra); }
	@Transactional(readOnly = true) public Cotacao buscarComItens(Long codigo) { return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Cotação não encontrada")); }
}
