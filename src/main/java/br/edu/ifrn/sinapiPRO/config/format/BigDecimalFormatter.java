package br.edu.ifrn.sinapiPRO.config.format;

import java.math.BigDecimal;
import java.util.Locale;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class BigDecimalFormatter extends NumberFormatter<BigDecimal> {
	
	private final Environment env;

	public BigDecimalFormatter(Environment env) {
		this.env = env;
	}

	@Override
	public String pattern(Locale locale) {
		return env.getProperty("bigdecimal.format", "#,##0.00");
	}
}
