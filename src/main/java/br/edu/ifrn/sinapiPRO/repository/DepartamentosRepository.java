package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Departamento;
import br.edu.ifrn.sinapiPRO.repository.helper.departamento.DepartamentosRepositoryQueries;
@Repository
public interface DepartamentosRepository extends JpaRepository<Departamento, Long>, DepartamentosRepositoryQueries {
	Optional<Departamento> findByNomeIgnoreCase(String nome);
}
