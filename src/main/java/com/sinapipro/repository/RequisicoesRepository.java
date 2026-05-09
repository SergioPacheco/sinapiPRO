package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Requisicao;
import com.sinapipro.repository.support.ObraScopedRepository;
@Repository
public interface RequisicoesRepository extends JpaRepository<Requisicao, Long>, ObraScopedRepository<Requisicao> {
	List<Requisicao> findByObraCodigoOrderByDataRequisicaoDesc(Long codigoObra);

	@Override
	default List<Requisicao> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByDataRequisicaoDesc(codigoObra);
	}
}
