package com.order.management.paymentservice.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

import static com.order.management.common.constant.PaymentType.BANK_TRANSFER;
import static com.order.management.common.constant.PaymentType.CREDIT_CARD;

@Configuration
public class RabbitmqSchemaConfig {
	@Bean
	Declarables createValidationSchema(@Value("${order.payment.queue}") String paymentQueueName,
									   @Value("${order.payment.exchange}") String paymentExchangeName) {
		TopicExchange topicExchange = new TopicExchange(paymentExchangeName, true,
				false, null);

		Queue queue = new Queue(paymentQueueName);

		Binding bindingCreditCard = BindingBuilder.bind(queue).to(topicExchange)
				.with(CREDIT_CARD.getKey());

		Binding bindingBankTransfer = BindingBuilder.bind(queue).to(topicExchange)
				.with(BANK_TRANSFER.getKey());

		Binding bindingWallets = BindingBuilder.bind(queue).to(topicExchange)
				.with("payment.wallet.*");


		return new Declarables(topicExchange, queue, bindingCreditCard, bindingBankTransfer, bindingWallets);
	}
}
