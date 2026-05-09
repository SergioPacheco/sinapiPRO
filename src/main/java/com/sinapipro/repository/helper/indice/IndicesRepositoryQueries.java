package com.sinapipro.repository.helper.indice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.Indice;
import com.sinapipro.repository.filter.IndiceFilter;
public interface IndicesRepositoryQueries { Page<Indice> filtrar(IndiceFilter f, Pageable p);
}
