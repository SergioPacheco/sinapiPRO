package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.DiarioObra;
import com.sinapipro.repository.support.ObraScopedRepository;
@Repository
public interface DiarioObraRepository extends JpaRepository<DiarioObra, Long>, ObraScopedRepository<DiarioObra> {
	List<DiarioObra> findByObraCodigoOrderByDataDesc(Long codigoObra);

	@Override
	default List<DiarioObra> findByObraScopeCodigo(Long codigoObra) {
		return findByObraCodigoOrderByDataDesc(codigoObra);
	}
}
