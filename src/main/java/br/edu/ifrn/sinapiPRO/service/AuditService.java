package br.edu.ifrn.sinapiPRO.service;

import java.time.LocalDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import br.edu.ifrn.sinapiPRO.model.AuditLog;
import br.edu.ifrn.sinapiPRO.repository.AuditLogRepository;

@Service
public class AuditService {

	private final AuditLogRepository auditLogRepository;

	public AuditService(AuditLogRepository auditLogRepository) {
		this.auditLogRepository = auditLogRepository;
	}

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void registrar(String entidade, Long codigoEntidade, String acao, String detalhes) {
		AuditLog log = new AuditLog();
		log.setEntidade(entidade);
		log.setCodigoEntidade(codigoEntidade);
		log.setAcao(acao);
		log.setUsuario(getUsuarioAtual());
		log.setDataHora(LocalDateTime.now());
		log.setDetalhes(detalhes);
		auditLogRepository.save(log);
	}

	private String getUsuarioAtual() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return auth != null ? auth.getName() : "sistema";
	}
}
