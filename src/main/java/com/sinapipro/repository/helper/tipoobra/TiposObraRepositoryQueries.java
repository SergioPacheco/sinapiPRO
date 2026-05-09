package com.sinapipro.repository.helper.tipoobra;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.TipoObra;
import com.sinapipro.repository.filter.TipoObraFilter;
public interface TiposObraRepositoryQueries { Page<TipoObra> filtrar(TipoObraFilter f, Pageable p);
}
