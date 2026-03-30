package br.edu.ifrn.sinapiPRO.repository.helper.indice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.Indice;
import br.edu.ifrn.sinapiPRO.repository.filter.IndiceFilter;
public interface IndicesRepositoryQueries { Page<Indice> filtrar(IndiceFilter f, Pageable p);
}
