/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      https://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hadoop.admin.message;

import java.util.Map;

import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.hadoop.admin.entity.WorkerNode;
import org.springframework.data.hadoop.admin.entity.WorkflowLaunchRequest;
import org.springframework.util.Assert;

/**
 * @author Jarred Li
 *
 */
public class MessageProducer implements ApplicationContextAware, InitializingBean {

	private ApplicationContext context;

	private String exchangeName = "ex_spring_hadoop_admin";

	private Map<String, String> queueBindings;

	/**
	 * 
	 */
	public void init() {
		AmqpAdmin admin = context.getBean("admin", AmqpAdmin.class);
		Exchange exchange = new TopicExchange(exchangeName);
		admin.declareExchange(exchange);

		for (Map.Entry<String, String> entry : queueBindings.entrySet()) {
			Queue queue = new Queue(entry.getKey());
			admin.declareQueue(queue);
			Binding binding = new Binding(entry.getKey(), Binding.DestinationType.QUEUE, exchangeName,
					entry.getValue(), null);
			admin.declareBinding(binding);
		}
	}

	public void send(WorkflowLaunchRequest launchRequest, WorkerNode node) {
		ConnectionFactory connectionFactory = context.getBean("connectionFactory", ConnectionFactory.class);
		RabbitTemplate template = new RabbitTemplate(connectionFactory);
		template.setExchange(exchangeName);
		template.convertAndSend(node.getQueueName(), launchRequest);

	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;

	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(exchangeName);
	}

	/**
	 * @return the exchangeName
	 */
	public String getExchangeName() {
		return exchangeName;
	}

	/**
	 * @param exchangeName the exchangeName to set
	 */
	public void setExchangeName(String exchangeName) {
		this.exchangeName = exchangeName;
	}

	/**
	 * @return the queueBindings
	 */
	public Map<String, String> getQueueBindings() {
		return queueBindings;
	}

	/**
	 * @param queueBindings the queueBindings to set
	 */
	public void setQueueBindings(Map<String, String> queueBindings) {
		this.queueBindings = queueBindings;
	}

}
