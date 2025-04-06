package com.order.management.orderservice.helper.rabbitmq;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class RabbitmqHeaderXDeath {
	private int count;
	private String exchange;
	private String queue;
	private String reason;
	private List<String> routingKeys;
	private Date time;
}
