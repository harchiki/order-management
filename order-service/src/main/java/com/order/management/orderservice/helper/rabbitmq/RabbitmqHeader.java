package com.order.management.orderservice.helper.rabbitmq;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Referenced by Timotius Pamungkas from RabbitMQ trainer on Udemy
 * <a href="https://www.udemy.com/course/rabbitmq-java-spring-boot-for-system-integration/learn/lecture/41104776#overview">rabbitmq-java-spring-boot</a>
 */
@Getter
@Setter
public class RabbitmqHeader {
	private static final String KEYWORD_QUEUE_WAIT = "wait";
	private List<RabbitmqHeaderXDeath> xDeaths = new ArrayList<>(2);
	private String xFirstDeathExchange = "";
	private String xFirstDeathQueue = "";
	private String xFirstDeathReason = "";

	@SuppressWarnings("unchecked")
	public RabbitmqHeader(Map<String, Object> headers) {
		if (MapUtils.isNotEmpty(headers)) {
			Optional.ofNullable(headers.get("x-first-death-exchange"))
					.ifPresent(s -> setXFirstDeathQueue(s.toString()));

			Optional.ofNullable(headers.get("x-first-death-queue"))
					.ifPresent(s -> setXFirstDeathQueue(s.toString()));

			Optional.ofNullable(headers.get("x-first-death-reason"))
					.ifPresent(s -> setXFirstDeathReason(s.toString()));

			List<Map<String, Object>> xDeathHeaders = (List<Map<String, Object>>) headers.get("x-death");

			if (CollectionUtils.isNotEmpty(xDeathHeaders)) {
				for (Map<String, Object> x : xDeathHeaders) {
					RabbitmqHeaderXDeath hdrDeath = new RabbitmqHeaderXDeath();

					Optional.ofNullable(x.get("reason")).ifPresent(s -> hdrDeath.setReason(s.toString()));

					Optional.ofNullable(x.get("count"))
							.ifPresent(s -> hdrDeath.setCount(Integer.parseInt(s.toString())));

					Optional.ofNullable(x.get("exchange")).ifPresent(s -> hdrDeath.setExchange(s.toString()));

					Optional.ofNullable(x.get("queue")).ifPresent(s -> hdrDeath.setQueue(s.toString()));

					Optional.ofNullable(x.get("routing-keys")).ifPresent(r -> {
								List<String> listR = (List<String>) r;
								hdrDeath.setRoutingKeys(listR);
							});

					Optional.ofNullable(x.get("time")).ifPresent(d -> hdrDeath.setTime((Date) d));

					xDeaths.add(hdrDeath);
				}
			}
		}
	}

	public int getFailedRetryCount() {
		// get from queue "wait"
		for (var xDeath : xDeaths) {
			if (xDeath.getExchange().toLowerCase().endsWith(KEYWORD_QUEUE_WAIT)
					&& xDeath.getQueue().toLowerCase().endsWith(KEYWORD_QUEUE_WAIT)) {
				return xDeath.getCount();
			}
		}
		return 0;
	}
}
