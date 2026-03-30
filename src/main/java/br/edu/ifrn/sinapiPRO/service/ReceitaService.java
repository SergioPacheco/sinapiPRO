package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Receita;
import br.edu.ifrn.sinapiPRO.repository.ReceitasRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class ReceitaService {
	@Autowired
	private ReceitasRepository repository;
	@Transactional
	public Receita salvar(Receita r) {
		r.getRecebimentos().forEach(rec -> rec.setReceita(r));
		BigDecimal totalRecebido = r.getRecebimentos().stream().map(rec -> rec.getValorRecebido()).reduce(BigDecimal.ZERO, BigDecimal::add);
		if (totalRecebido.compareTo(r.getValor()) >= 0) r.setSituacao("RECEBIDA");
		else if (totalRecebido.signum() > 0) r.setSituacao("PARCIAL");
		return repository.saveAndFlush(r);
	}
	@Transactional
	public void excluir(Long c) {
		try {
			repository.deleteById(c);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar a receita.");
		}
	}

	@Transactional(readOnly = true)
	public List<Receita> findAbertas() {
		return repository.findBySituacaoOrderByDataVencimentoAsc("ABERTA");
	}

	@Transactional(readOnly = true)
	public List<Receita> findAll() {
		return repository.findAll();
	}

	@Transactional(readOnly = true)
	public Receita buscarComRecebimentos(Long codigo) {
		return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Receita não encontrada"));
	}
}
