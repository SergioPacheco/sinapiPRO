package br.edu.ifrn.sinapiPRO.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.TabelaPreco;
import br.edu.ifrn.sinapiPRO.repository.TabelasPrecosRepository;
import br.edu.ifrn.sinapiPRO.service.support.AbstractObraScopedCrudService;

@Service
public class TabelaPrecoService extends AbstractObraScopedCrudService<TabelaPreco, TabelasPrecosRepository> {

    private final TabelasPrecosRepository repository;

    public TabelaPrecoService(TabelasPrecosRepository repository) {
        super(repository, "Impossível apagar a tabela de preços.", "Tabela não encontrada.");
        this.repository = repository;
    }

    @Override
    @Transactional
    public TabelaPreco salvar(TabelaPreco tabela) {
        tabela.getItens().forEach(item -> item.setTabela(tabela));
        return repository.saveAndFlush(tabela);
    }

    @Transactional
    public TabelaPreco aplicarReajuste(Long codigoTabela, BigDecimal percentual) {
        TabelaPreco tabela = buscarPorCodigo(codigoTabela);
        BigDecimal fator = BigDecimal.ONE.add(
                percentual.divide(BigDecimal.valueOf(100), 6, RoundingMode.HALF_UP));
        tabela.getItens().forEach(item ->
                item.setValor(item.getValor().multiply(fator).setScale(2, RoundingMode.HALF_UP)));
        return repository.saveAndFlush(tabela);
    }

    @Transactional(readOnly = true)
    public TabelaPreco buscarComItens(Long codigo) {
        return buscarPorCodigo(codigo);
    }
}
