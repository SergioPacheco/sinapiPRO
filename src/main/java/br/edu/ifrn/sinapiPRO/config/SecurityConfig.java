package br.edu.ifrn.sinapiPRO.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import br.edu.ifrn.sinapiPRO.security.AppUserDetailsService;
import br.edu.ifrn.sinapiPRO.security.PrimeiroAcessoFilter;

@EnableWebSecurity
@ComponentScan(basePackageClasses = AppUserDetailsService.class)
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	private UserDetailsService userDetailsService;

	@Autowired
	private PrimeiroAcessoFilter primeiroAcessoFilter;

	@Autowired
	private br.edu.ifrn.sinapiPRO.security.LoginSuccessHandler loginSuccessHandler;
	
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth
			.userDetailsService(userDetailsService)
			.passwordEncoder(passwordEncoder());
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		web.ignoring()
			.antMatchers("/layout/**")
			.antMatchers("/images/**");
	}
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
			.authorizeRequests()
				// Orçamento
				.antMatchers("/orcamentos/novo").hasRole("CADASTRAR_ORCAMENTO")
				// Usuários
				.antMatchers("/usuarios/**").hasRole("CADASTRAR_USUARIO")
				// Financeiro
				.antMatchers("/despesas/**", "/receitas/**",
						"/movimentosBancarios/**", "/conciliacao/**",
						"/boletos/**", "/cheques/**",
						"/planoContas/**", "/contasBancarias/**").hasAnyRole("FINANCEIRO", "ADMIN")
				// Comercial
				.antMatchers("/vendas/**", "/propostas/**",
						"/unidadesVenda/**", "/situacoesUnidade/**",
						"/tabelasPrecos/**", "/comissoes/**").hasAnyRole("COMERCIAL", "ADMIN")
				// Suprimentos
				.antMatchers("/cotacoes/**", "/pedidosCompra/**",
						"/estoque/**", "/requisicoes/**").hasAnyRole("SUPRIMENTOS", "ADMIN")
				// Obras / Operacional
				.antMatchers("/contratos/**", "/medicoes/**",
						"/diarioObra/**", "/bancoHoras/**",
						"/competencias/**", "/prestacaoContas/**").hasAnyRole("OBRAS", "ADMIN")
				// RH
				.antMatchers("/funcionarios/**", "/departamentos/**",
						"/cargos/**", "/funcoes/**").hasAnyRole("RH", "ADMIN")
				// Frota / GED
				.antMatchers("/frota/**", "/ged/**").hasAnyRole("OBRAS", "ADMIN")
				// Atendimento
				.antMatchers("/atendimentos/**").hasAnyRole("ATENDIMENTO", "ADMIN")
				// Faturamento
				.antMatchers("/notasFiscaisServico/**").hasAnyRole("FINANCEIRO", "ADMIN")
				// Acesso geral autenticado
				.antMatchers("/trocarSenha").authenticated()
				.anyRequest().authenticated()
				.and()
			.formLogin()
				.loginPage("/login")
				.successHandler(loginSuccessHandler)
				.permitAll()
				.and()
			.logout()
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.and()
			.exceptionHandling()
				.accessDeniedPage("/403")
				.and()
			.sessionManagement()
				.invalidSessionUrl("/login");

		http.addFilterAfter(primeiroAcessoFilter, UsernamePasswordAuthenticationFilter.class);
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
