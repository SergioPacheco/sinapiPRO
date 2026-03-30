package br.edu.ifrn.sinapiPRO.repository.helper.tipoobra;
import org.springframework.data.domain.Page; import org.springframework.data.domain.Pageable;
import br.edu.ifrn.sinapiPRO.model.TipoObra; import br.edu.ifrn.sinapiPRO.repository.filter.TipoObraFilter;
public interface TiposObraRepositoryQueries { Page<TipoObra> filtrar(TipoObraFilter f, Pageable p); }
