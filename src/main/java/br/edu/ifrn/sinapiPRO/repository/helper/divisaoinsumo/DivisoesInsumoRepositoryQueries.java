package br.edu.ifrn.sinapiPRO.repository.helper.divisaoinsumo;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.DivisaoInsumo; import br.edu.ifrn.sinapiPRO.repository.filter.DivisaoInsumoFilter;
public interface DivisoesInsumoRepositoryQueries { Page<DivisaoInsumo> filtrar(DivisaoInsumoFilter f, Pageable p); }
