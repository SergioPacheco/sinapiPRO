package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.UnidadeVenda;
import br.edu.ifrn.sinapiPRO.repository.support.ObraScopedRepository;

@Repository
public interface UnidadesVendaRepository extends JpaRepository<UnidadeVenda, Long>, ObraScopedRepository<UnidadeVenda> {
	List<UnidadeVenda> findByObraCodigoOrderByIdentificacaoAsc(Long codigoObra);

	@Override
	default List<UnidadeVenda> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByIdentificacaoAsc(codigoObra);
	}
}
