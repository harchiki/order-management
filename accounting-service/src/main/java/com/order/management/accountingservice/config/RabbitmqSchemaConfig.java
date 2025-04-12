package com.order.management.accountingservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqSchemaConfig {
	@Bean
	Declarables createPriceSchema(@Value("${order.accounting.price.queue}") String priceQueueName,
								  @Value("${order.accounting.price.exchange}") String priceQueueExchangeName) {
		DirectExchange directExchange = new DirectExchange(priceQueueExchangeName, true,
				false, null);

		Queue priceQueue = new Queue(priceQueueName);
		Binding binding = BindingBuilder.bind(priceQueue).to(directExchange).withQueueName();
		return new Declarables(directExchange, priceQueue, binding);
	}


}
