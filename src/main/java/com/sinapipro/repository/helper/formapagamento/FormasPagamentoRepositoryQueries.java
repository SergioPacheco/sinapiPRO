package com.sinapipro.repository.helper.formapagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.FormaPagamento;
import com.sinapipro.repository.filter.FormaPagamentoFilter;
public interface FormasPagamentoRepositoryQueries { Page<FormaPagamento> filtrar(FormaPagamentoFilter f, Pageable p);
}
