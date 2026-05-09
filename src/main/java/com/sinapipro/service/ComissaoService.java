package com.sinapipro.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sinapipro.model.Comissao;
import com.sinapipro.model.Venda;
import com.sinapipro.repository.ComissoesRepository;
import com.sinapipro.service.support.AbstractSimpleCrudService;

@Service
public class ComissaoService extends AbstractSimpleCrudService<Comissao, ComissoesRepository> {

    private final ComissoesRepository repository;

    public ComissaoService(ComissoesRepository repository) {
        super(repository, "Impossível apagar a comissão.", "Comissão não encontrada.");
        this.repository = repository;
    }

    @Override
    @Transactional
    public Comissao salvar(Comissao comissao) {
        if (comissao.getPercentual() != null && comissao.getVenda() != null) {
            Venda venda = comissao.getVenda();
            BigDecimal valorCalculado = venda.getValorVenda()
                    .multiply(comissao.getPercentual())
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            comissao.setValor(valorCalculado);
        }
        return repository.saveAndFlush(comissao);
    }

    @Transactional(readOnly = true)
    public List<Comissao> findByVenda(Long codigoVenda) {
        return repository.findByVendaCodigoOrderByNomeCorretorAsc(codigoVenda);
    }

    @Transactional(readOnly = true)
    public List<Comissao> findPendentes() {
        return repository.findBySituacaoOrderByDataPagamentoAsc("PENDENTE");
    }
}
