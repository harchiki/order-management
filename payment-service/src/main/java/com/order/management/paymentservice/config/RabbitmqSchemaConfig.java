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
	Declarables createValidationSchema(@Value("${order.payment.credit-card.queue}") String paymentCreditCardQueueName,
									   @Value("${order.payment.bank-transfer.queue}") String paymentBankTransferQueueName,
									   @Value("${order.payment.wallet.queue}") String paymentWalletQueueName,
									   @Value("${order.payment.exchange}") String paymentExchangeName) {
		TopicExchange topicExchange = new TopicExchange(paymentExchangeName, true,
				false, null);

		Queue queueCreditCard = new Queue(paymentCreditCardQueueName);
		Binding bindingCreditCard = BindingBuilder.bind(queueCreditCard).to(topicExchange)
				.with(CREDIT_CARD.getKey());

		Queue queueBankTransfer = new Queue(paymentBankTransferQueueName);
		Binding bindingBankTransfer = BindingBuilder.bind(queueBankTransfer).to(topicExchange)
				.with(BANK_TRANSFER.getKey());

		Queue queueWallet = new Queue(paymentWalletQueueName);
		Binding bindingWallets = BindingBuilder.bind(queueWallet).to(topicExchange)
				.with("payment.wallet.*");


		return new Declarables(topicExchange, queueCreditCard, queueBankTransfer, queueWallet,
				bindingCreditCard, bindingBankTransfer, bindingWallets);
	}
}
