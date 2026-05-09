package com.sinapipro.repository.helper.empresa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.Empresa;
import com.sinapipro.repository.filter.EmpresaFilter;
public interface EmpresasRepositoryQueries { Page<Empresa> filtrar(EmpresaFilter f, Pageable p);
}
