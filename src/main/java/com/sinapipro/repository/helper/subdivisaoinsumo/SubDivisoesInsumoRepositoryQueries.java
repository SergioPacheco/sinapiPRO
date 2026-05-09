package com.sinapipro.repository.helper.subdivisaoinsumo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.SubDivisaoInsumo;
import com.sinapipro.repository.filter.SubDivisaoInsumoFilter;
public interface SubDivisoesInsumoRepositoryQueries { Page<SubDivisaoInsumo> filtrar(SubDivisaoInsumoFilter f, Pageable p);
}
