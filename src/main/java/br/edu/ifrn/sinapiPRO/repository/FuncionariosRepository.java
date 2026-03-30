package br.edu.ifrn.sinapiPRO.repository;
import java.util.List; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Funcionario; import br.edu.ifrn.sinapiPRO.repository.helper.funcionario.FuncionariosRepositoryQueries;
@Repository public interface FuncionariosRepository extends JpaRepository<Funcionario, Long>, FuncionariosRepositoryQueries {
	List<Funcionario> findByAtivoTrue(); }
