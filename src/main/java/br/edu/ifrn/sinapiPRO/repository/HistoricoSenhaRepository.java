package br.edu.ifrn.sinapiPRO.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import br.edu.ifrn.sinapiPRO.model.HistoricoSenha;
import br.edu.ifrn.sinapiPRO.model.Usuario;

public interface HistoricoSenhaRepository extends JpaRepository<HistoricoSenha, Long> {
	List<HistoricoSenha> findTop3ByUsuarioOrderByDataCriacaoDesc(Usuario usuario);
}
