package com.sinapipro.repository.helper.funcionario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sinapipro.model.Funcionario;
import com.sinapipro.repository.filter.FuncionarioFilter;
public interface FuncionariosRepositoryQueries { Page<Funcionario> filtrar(FuncionarioFilter f, Pageable p);
}
