package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Empresa; import br.edu.ifrn.sinapiPRO.repository.helper.empresa.EmpresasRepositoryQueries;
@Repository public interface EmpresasRepository extends JpaRepository<Empresa, Long>, EmpresasRepositoryQueries {
	Optional<Empresa> findByNomeIgnoreCase(String nome); }
