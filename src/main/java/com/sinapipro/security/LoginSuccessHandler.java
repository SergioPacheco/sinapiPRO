package com.sinapipro.security;

import java.io.IOException;
import java.time.LocalDateTime;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.sinapipro.model.Usuario;
import com.sinapipro.repository.UsuariosRepository;

@Component
public class LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

	private final UsuariosRepository usuariosRepository;

	public LoginSuccessHandler(UsuariosRepository usuariosRepository) {
		this.usuariosRepository = usuariosRepository;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
										Authentication authentication) throws IOException, ServletException {
		if (authentication.getPrincipal() instanceof UsuarioSistema) {
			UsuarioSistema us = (UsuarioSistema) authentication.getPrincipal();
			usuariosRepository.findByEmail(us.getUsername()).ifPresent(u -> {
				u.setDataUltimoAcesso(LocalDateTime.now());
				u.setConfirmacaoSenha(u.getSenha());
				usuariosRepository.save(u);
			});
		}
		super.onAuthenticationSuccess(request, response, authentication);
	}
}
