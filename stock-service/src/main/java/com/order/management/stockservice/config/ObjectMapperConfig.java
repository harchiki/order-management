package com.order.management.stockservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ObjectMapperConfig {
	@Bean
	ObjectMapper objectMapper() {
		return JsonMapper.builder().findAndAddModules().build();
	}

	@Bean
	Jackson2JsonMessageConverter converter(ObjectMapper objectMapper) {
		return new Jackson2JsonMessageConverter(objectMapper);
	}
}
