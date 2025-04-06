package com.order.management.orderservice.helper.rabbitmq;

import com.rabbitmq.client.Channel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Referenced by Timotius Pamungkas from RabbitMQ trainer on Udemy
 * <a href="https://www.udemy.com/course/rabbitmq-java-spring-boot-for-system-integration/learn/lecture/41104776#overview">rabbitmq-java-spring-boot</a>
 */
@Slf4j
@Getter
@Setter
public class UnprocessableMessageHandler {
	private String deadExchangeName;
	private int maxRetryCount = 3;

	public UnprocessableMessageHandler(String deadExchangeName) throws IllegalArgumentException {
		super();
		if (deadExchangeName == null || deadExchangeName.isEmpty()) {
			throw new IllegalArgumentException("Must define dlx exchange name");
		}

		this.deadExchangeName = deadExchangeName;
	}

	public UnprocessableMessageHandler(String deadExchangeName, int maxRetryCount) {
		this(deadExchangeName);
		setMaxRetryCount(maxRetryCount);
	}


	public void handleErrorProcessingMessage(Message message, Channel channel, long deliveryTag) {
		var rabbitMqHeader = new RabbitmqHeader(message.getMessageProperties().getHeaders());

		try {
			if (rabbitMqHeader.getFailedRetryCount() >= maxRetryCount) {
				// publish to dead and ack
                log.warn("[DEAD] Error at {} on retry {} for message {}",
						LocalDateTime.now(), rabbitMqHeader.getFailedRetryCount(), new String(message.getBody()));

				channel.basicPublish(getDeadExchangeName(), message.getMessageProperties().getReceivedRoutingKey(),
						null, message.getBody());
				channel.basicAck(deliveryTag, false);
			} else {
                log.warn("[REQUEUE] Error at {} on retry {} for message {}",
						LocalDateTime.now(), rabbitMqHeader.getFailedRetryCount(), new String(message.getBody()));

				channel.basicReject(deliveryTag, false);
			}
		} catch (IOException e) {
            log.warn("[HANDLER-FAILED] Error at {} on retry {} for message {}",
					LocalDateTime.now(), rabbitMqHeader.getFailedRetryCount(), new String(message.getBody()));
		}

	}

	public void setMaxRetryCount(int maxRetryCount) throws IllegalArgumentException {
		if (maxRetryCount > 1000) {
			throw new IllegalArgumentException("max retry must between 0-1000");
		}

		this.maxRetryCount = maxRetryCount;
	}
}
