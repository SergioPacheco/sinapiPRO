package br.edu.ifrn.sinapiPRO.repository.helper.formapagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.FormaPagamento;
import br.edu.ifrn.sinapiPRO.repository.filter.FormaPagamentoFilter;
public interface FormasPagamentoRepositoryQueries { Page<FormaPagamento> filtrar(FormaPagamentoFilter f, Pageable p);
}
