package com.sinapipro.config.format;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class LocalTimeFormatter extends TemporalFormatter<LocalTime> {
	
	private final Environment env;

	public LocalTimeFormatter(Environment env) {
		this.env = env;
	}

	@Override
	public String pattern(Locale locale) {
		return env.getProperty("localdate.format-" + locale, "HH:mm");
	}

	@Override
	public LocalTime parse(String text, DateTimeFormatter formatter) {
		return LocalTime.parse(text, formatter);
	}	
}
