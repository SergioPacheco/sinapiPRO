package br.edu.ifrn.sinapiPRO.service;
import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Requisicao; import br.edu.ifrn.sinapiPRO.repository.RequisicoesRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class RequisicaoService {
	@Autowired private RequisicoesRepository repository;
	@Transactional public Requisicao salvar(Requisicao r) {
		r.getItens().forEach(i -> i.setRequisicao(r));
		return repository.saveAndFlush(r); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar a requisição."); } }
	@Transactional(readOnly = true) public List<Requisicao> findByObra(Long codigoObra) { return repository.findByObraCodigoOrderByDataRequisicaoDesc(codigoObra); }
	@Transactional(readOnly = true) public Requisicao buscarComItens(Long codigo) { return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Requisição não encontrada")); }
}
