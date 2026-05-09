package com.sinapipro.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.sinapipro.model.Notificacao;
@Repository
public interface NotificacoesRepository extends JpaRepository<Notificacao, Long> {
List<Notificacao> findByLidaFalseOrderByDataCriacaoDesc();
}
