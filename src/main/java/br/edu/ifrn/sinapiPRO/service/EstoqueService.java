package br.edu.ifrn.sinapiPRO.service;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.Estoque;
import br.edu.ifrn.sinapiPRO.model.MovimentoEstoque;
import br.edu.ifrn.sinapiPRO.repository.EstoqueRepository;
@Service
public class EstoqueService {
	@Autowired
	private EstoqueRepository repository;
	@Transactional
	public Estoque salvar(Estoque e) {
		return repository.saveAndFlush(e);
	}

	@Transactional
	public Estoque movimentar(Long codigoEstoque, MovimentoEstoque movimento) {
		Estoque estoque = repository.findById(codigoEstoque).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
		movimento.setEstoque(estoque);
		estoque.getMovimentos().add(movimento);
		BigDecimal qtd = movimento.getQuantidade();
		if ("ENTRADA".equals(movimento.getTipo())) estoque.setQuantidadeAtual(estoque.getQuantidadeAtual().add(qtd));
		else if ("SAIDA".equals(movimento.getTipo())) estoque.setQuantidadeAtual(estoque.getQuantidadeAtual().subtract(qtd));
		else estoque.setQuantidadeAtual(qtd); // AJUSTE
		return repository.saveAndFlush(estoque);
	}
	@Transactional(readOnly = true)
	public List<Estoque> findByObra(Long codigoObra) {
		return repository.findByObraCodigo(codigoObra);
	}

	@Transactional(readOnly = true)
	public Estoque buscarComMovimentos(Long codigo) {
		return repository.findById(codigo).orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
	}
}
