package com.sinapipro.repository.helper.divisaoinsumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.DivisaoInsumo;
import com.sinapipro.repository.filter.DivisaoInsumoFilter;
public interface DivisoesInsumoRepositoryQueries { Page<DivisaoInsumo> filtrar(DivisaoInsumoFilter f, Pageable p);
}
