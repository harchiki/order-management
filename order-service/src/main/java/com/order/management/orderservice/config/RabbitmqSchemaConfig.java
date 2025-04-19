package com.order.management.orderservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

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
	Declarables createValidationResponseSchema(@Value("${order.validation.response.queue}") String validationResponseQueueName,
											   @Value("${order.validation.response.exchange}") String validationExchangeName,
											   @Value("${order.validation.response.wait.exchange}") String dlxWaitExchangeName) {
		FanoutExchange validationExchange = new FanoutExchange(validationExchangeName, true, false, null);

		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", dlxWaitExchangeName);
		Queue validationResponeQueue = new Queue(validationResponseQueueName, true,false, false, args);
		Binding binding = BindingBuilder.bind(validationResponeQueue).to(validationExchange);



		return new Declarables(validationExchange, validationResponeQueue, binding);
	}

	@Bean
	Declarables createValidationResponseWaitingSchema(@Value("${order.validation.response.wait.queue}") String waitingQueueName,
													  @Value("${order.validation.response.wait.exchange}") String waitingExchangeName,
													  @Value("${order.validation.response.exchange}") String dlxValidationExchangeName) {
		FanoutExchange waitExchange = new FanoutExchange(waitingExchangeName, true, false, null);

		Map<String, Object> args = new HashMap<>();
		args.put("x-dead-letter-exchange", dlxValidationExchangeName);
//		args.put("x-dead-letter-routing-key", "dlxValidationExchangeName");
		args.put("x-message-ttl", 1000);

		Queue validationResponeQueue = new Queue(waitingQueueName, true,false, false, args);
		Binding binding = BindingBuilder.bind(validationResponeQueue).to(waitExchange);


		return new Declarables(waitExchange, validationResponeQueue, binding);
	}

	@Bean
	Declarables createRejectedOrderSchema(@Value("${order.rejected.queue}") String rejectedOrderQueueName,
										  @Value("${order.validation.response.dlx}") String rejectedExchangeName) {
		FanoutExchange rejectedExchange = new FanoutExchange(rejectedExchangeName, true, false, null);
		Queue validationResponeQueue = new Queue(rejectedOrderQueueName);
		Binding binding = BindingBuilder.bind(validationResponeQueue).to(rejectedExchange);
		return new Declarables(rejectedExchange, validationResponeQueue, binding);
	}

	@Bean
	Declarables createOrderStatusSchema(@Value("${order.status.queue}") String statusQueue,
									    @Value("${order.status.exchange}") String statusExchange,
									    @Value("${order.status.key}") String statusKey) {
		DirectExchange exchange = new DirectExchange(statusExchange, true, false, null);
		Queue queue = new Queue(statusQueue);
		Binding binding = BindingBuilder.bind(queue).to(exchange).with(statusKey);
		return new Declarables(exchange, queue, binding);
	}
}
