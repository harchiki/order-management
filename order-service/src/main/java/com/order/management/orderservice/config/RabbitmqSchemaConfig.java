package com.order.management.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqSchemaConfig {
	@Bean
	Declarables createValidationSchema(@Value("${order.validation.stock.queue}") String stockQueueName,
									   @Value("${order.validation.discount.queue}") String discountQueueName,
									   @Value("${order.validation.exchange}") String validationExchange) {
		FanoutExchange fanoutExchange = new FanoutExchange(validationExchange, true,
				false, null);

		Queue discountQueue = new Queue(discountQueueName);
		Binding discountBinding = BindingBuilder.bind(discountQueue).to(fanoutExchange);

		Queue stockQueue = new Queue(stockQueueName);
		Binding stockBinding = BindingBuilder.bind(stockQueue).to(fanoutExchange);

		return new Declarables(fanoutExchange, discountQueue, discountBinding, stockQueue, stockBinding);
	}

	@Bean
	Declarables createValidationResponseSchema(@Value("${order.validation.response.queue:q.validation.response}") String stockQueueName,
											   @Value("${order.validation.response.exchange:x.validation.response}") String validationExchange) {
		FanoutExchange fanoutExchange = new FanoutExchange(validationExchange, true,
				false, null);

		Queue stockQueue = new Queue(stockQueueName);
		Binding stockBinding = BindingBuilder.bind(stockQueue).to(fanoutExchange);

		return new Declarables(fanoutExchange, stockQueue, stockBinding);
	}
}
