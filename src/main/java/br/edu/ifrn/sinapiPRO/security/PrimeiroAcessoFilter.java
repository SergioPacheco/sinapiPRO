package br.edu.ifrn.sinapiPRO.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class PrimeiroAcessoFilter extends OncePerRequestFilter {

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
									FilterChain filterChain) throws ServletException, IOException {
		String uri = request.getRequestURI();
		if (uri.startsWith("/layout") || uri.startsWith("/images") || uri.startsWith("/login")
				|| uri.startsWith("/logout") || uri.startsWith("/trocarSenha")) {
			filterChain.doFilter(request, response);
			return;
		}
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth != null && auth.getPrincipal() instanceof UsuarioSistema) {
			UsuarioSistema us = (UsuarioSistema) auth.getPrincipal();
			Boolean primeiro = us.getUsuario().getPrimeiroAcesso();
			if (primeiro != null && primeiro) {
				response.sendRedirect(request.getContextPath() + "/trocarSenha");
				return;
			}
		}
		filterChain.doFilter(request, response);
	}
}
