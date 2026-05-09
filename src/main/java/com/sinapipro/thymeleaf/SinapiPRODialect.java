package com.sinapipro.thymeleaf;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.thymeleaf.dialect.AbstractProcessorDialect;
import org.thymeleaf.processor.IProcessor;
import org.thymeleaf.standard.StandardDialect;

import com.sinapipro.thymeleaf.processor.ClassForErrorAttributeTagProcessor;
import com.sinapipro.thymeleaf.processor.MenuAttributeTagProcessor;
import com.sinapipro.thymeleaf.processor.MessageElementTagProcessor;
import com.sinapipro.thymeleaf.processor.OrderElementTagProcessor;
import com.sinapipro.thymeleaf.processor.PaginationElementTagProcessor;

@Component
public class SinapiPRODialect extends AbstractProcessorDialect {

	public SinapiPRODialect() {
		super("Sinapi PRO", "sinapiPRO", StandardDialect.PROCESSOR_PRECEDENCE);
	}
	
	@Override
	public Set<IProcessor> getProcessors(String dialectPrefix) {
		final Set<IProcessor> processadores = new HashSet<>();
		processadores.add(new ClassForErrorAttributeTagProcessor(dialectPrefix));
		processadores.add(new MessageElementTagProcessor(dialectPrefix));
		processadores.add(new OrderElementTagProcessor(dialectPrefix));
		processadores.add(new PaginationElementTagProcessor(dialectPrefix));
		processadores.add(new MenuAttributeTagProcessor(dialectPrefix));
		return processadores;
	}
}
