package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Contrato;
import br.edu.ifrn.sinapiPRO.repository.ContratosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.*;
@Service
public class ContratoService {
	@Autowired
	private ContratosRepository repository;
	@Transactional
	public Contrato salvar(Contrato c) {
		// Recalcula valor total dos itens
		BigDecimal total = c.getItens().stream()
			.map(i -> { BigDecimal vt = i.getQuantidade().multiply(i.getValorUnitario()); i.setValorTotal(vt); return vt; })
			.reduce(BigDecimal.ZERO, BigDecimal::add);
		c.setValorTotal(total);
		c.getItens().forEach(i -> i.setContrato(c));
		return repository.saveAndFlush(c);
	}
	@Transactional
	public void excluir(Long codigo) {
		try {
			repository.deleteById(codigo);
			repository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar. Possui medições vinculadas.");
		}
	}

	@Transactional(readOnly = true)
	public List<Contrato> findByObra(Long codigoObra) {
		return repository.findByObraCodigoOrderByDescricaoAsc(codigoObra);
	}

	@Transactional(readOnly = true)
	public Contrato buscarComItens(Long codigo) {
		return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Contrato não encontrado"));
	}

	public List<Contrato> findAll() {
		return repository.findAll();
	}
}
