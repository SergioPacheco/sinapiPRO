package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Funcionario;
import com.sinapipro.repository.filter.FuncionarioFilter;
import com.sinapipro.repository.helper.funcionario.FuncionariosRepositoryQueries;
import com.sinapipro.repository.support.FilterableRepository;
@Repository
public interface FuncionariosRepository extends JpaRepository<Funcionario, Long>, FuncionariosRepositoryQueries,
		FilterableRepository<Funcionario, FuncionarioFilter> {
	List<Funcionario> findByAtivoTrue();
}
