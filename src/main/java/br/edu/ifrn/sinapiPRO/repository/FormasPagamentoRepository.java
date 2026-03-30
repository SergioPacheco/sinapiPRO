package br.edu.ifrn.sinapiPRO.repository;
import java.util.Optional; import org.springframework.data.jpa.repository.JpaRepository; import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.FormaPagamento; import br.edu.ifrn.sinapiPRO.repository.helper.formapagamento.FormasPagamentoRepositoryQueries;
@Repository public interface FormasPagamentoRepository extends JpaRepository<FormaPagamento, Long>, FormasPagamentoRepositoryQueries {
	Optional<FormaPagamento> findByNomeIgnoreCase(String nome); }
