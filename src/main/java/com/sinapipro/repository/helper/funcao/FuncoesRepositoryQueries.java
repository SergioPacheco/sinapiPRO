package com.sinapipro.repository.helper.funcao;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.Funcao;
import com.sinapipro.repository.filter.FuncaoFilter;
public interface FuncoesRepositoryQueries { Page<Funcao> filtrar(FuncaoFilter f, Pageable p);
}
