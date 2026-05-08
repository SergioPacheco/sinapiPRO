package br.edu.ifrn.sinapiPRO.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.edu.ifrn.sinapiPRO.model.Requisicao;
import br.edu.ifrn.sinapiPRO.repository.support.ObraScopedRepository;
@Repository
public interface RequisicoesRepository extends JpaRepository<Requisicao, Long>, ObraScopedRepository<Requisicao> {
	List<Requisicao> findByObraCodigoOrderByDataRequisicaoDesc(Long codigoObra);

	@Override
	default List<Requisicao> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByDataRequisicaoDesc(codigoObra);
	}
}
