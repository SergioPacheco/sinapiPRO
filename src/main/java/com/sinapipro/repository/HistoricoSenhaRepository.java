package com.sinapipro.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sinapipro.model.HistoricoSenha;
import com.sinapipro.model.Usuario;

public interface HistoricoSenhaRepository extends JpaRepository<HistoricoSenha, Long> {
	List<HistoricoSenha> findTop3ByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
}
