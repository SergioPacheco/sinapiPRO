package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Funcao;
import br.edu.ifrn.sinapiPRO.repository.helper.funcao.FuncoesRepositoryQueries;
@Repository
public interface FuncoesRepository extends JpaRepository<Funcao, Long>, FuncoesRepositoryQueries {
	Optional<Funcao> findByNomeIgnoreCase(String nome);
}
