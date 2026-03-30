package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import javax.persistence.PersistenceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import br.edu.ifrn.sinapiPRO.model.TabelaPreco;
import br.edu.ifrn.sinapiPRO.repository.TabelasPrecosRepository;
import br.edu.ifrn.sinapiPRO.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class TabelaPrecoService {

    @Autowired
    private TabelasPrecosRepository repository;

    @Transactional
    public TabelaPreco salvar(TabelaPreco tabela) {
        tabela.getItens().forEach(item -> item.setTabela(tabela));
        return repository.saveAndFlush(tabela);
    }

    @Transactional
    public void excluir(Long codigo) {
        try {
            repository.deleteById(codigo);
            repository.flush();
        } catch (PersistenceException e) {
            throw new ImpossivelExcluirEntidadeException("Impossível apagar a tabela de preços.");
        }
    }

    @Transactional
    public TabelaPreco aplicarReajuste(Long codigoTabela, BigDecimal percentual) {
        TabelaPreco tabela = repository.findById(codigoTabela)
                .orElseThrow(() -> new RuntimeException("Tabela não encontrada"));
        BigDecimal fator = BigDecimal.ONE.add(
                percentual.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        tabela.getItens().forEach(item ->
                item.setValor(item.getValor().multiply(fator).setScale(2, RoundingMode.HALF_UP)));
        return repository.saveAndFlush(tabela);
    }

    @Transactional(readOnly = true)
    public List<TabelaPreco> findByObra(Long codigoObra) {
        return repository.findByObraCodigoAndAtivaTrue(codigoObra);
    }

    @Transactional(readOnly = true)
    public TabelaPreco buscarComItens(Long codigo) {
        return repository.findById(codigo)
                .orElseThrow(() -> new RuntimeException("Tabela não encontrada"));
    }

    public List<TabelaPreco> findAll() {
        return repository.findAll();
    }
}
