package com.sinapipro.repository.helper.departamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.Departamento;
import com.sinapipro.repository.filter.DepartamentoFilter;
public interface DepartamentosRepositoryQueries { Page<Departamento> filtrar(DepartamentoFilter f, Pageable p);
}
