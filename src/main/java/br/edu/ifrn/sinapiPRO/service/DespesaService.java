package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal; import java.time.LocalDate; import java.util.List; import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Despesa; import br.edu.ifrn.sinapiPRO.repository.DespesasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service public class DespesaService {
	@Autowired private DespesasRepository repository;
	@Transactional public Despesa salvar(Despesa d) {
		d.getPagamentos().forEach(p -> p.setDespesa(d));
		BigDecimal totalPago = d.getPagamentos().stream().map(p -> p.getValorPago()).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (totalPago.compareTo(d.getValor()) >= 0) d.setSituacao("PAGA");
		else if (totalPago.signum() > 0) d.setSituacao("PARCIAL");
		return repository.saveAndFlush(d); }
	@Transactional public void excluir(Long c) { try { repository.deleteById(c); repository.flush(); } catch (PersistenceException e) { throw new ImpossivelExcluirEntidadeException("Impossível apagar a despesa."); } }
	@Transactional(readOnly = true) public List<Despesa> findAbertas() { return repository.findBySituacaoOrderByDataVencimentoAsc("ABERTA"); }
	@Transactional(readOnly = true) public List<Despesa> findAll() { return repository.findAll(); }
	@Transactional(readOnly = true) public Despesa buscarComPagamentos(Long codigo) { return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Despesa não encontrada")); }
}
