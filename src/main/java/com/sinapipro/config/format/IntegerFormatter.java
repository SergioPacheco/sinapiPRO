package com.sinapipro.config.format;

import java.util.Locale;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class IntegerFormatter extends NumberFormatter<Integer> {
	
	private final Environment env;

	public IntegerFormatter(Environment env) {
		this.env = env;
	}

	@Override
	public String pattern(Locale locale) {
		return env.getProperty("bigdecimal.format", "#,##0");
	}
}
