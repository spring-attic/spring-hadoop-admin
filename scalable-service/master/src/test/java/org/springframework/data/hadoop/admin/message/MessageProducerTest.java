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

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.admin.entity.WorkerNode;
import org.springframework.data.hadoop.admin.entity.WorkflowLaunchRequest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jarred Li
 *
 */
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration
public class MessageProducerTest {

	//@Autowired
	private ApplicationContext ctx;
	
	private MessageProducer producer;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		ctx = new ClassPathXmlApplicationContext("META-INF/spring/hadoop-admin/rabbit-context.xml");
		producer = new MessageProducer();
		producer.setApplicationContext(ctx);
		Map<String,String> bindings = new HashMap<String,String>();
		bindings.put("node1.simple", "node1.simple");
		producer.setQueueBindings(bindings);
		producer.init();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		producer = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.message.MessageProducer#send(org.springframework.data.hadoop.admin.entity.WorkflowLaunchRequest, org.springframework.data.hadoop.admin.entity.WorkerNode)}.
	 */
	//@Test
	public void testSend() {
		WorkflowLaunchRequest request = new WorkflowLaunchRequest();
		request.setName("wordcount");
		WorkerNode node = new WorkerNode();
		node.setName("node1");
		node.setHostName("node1.example.com");
		node.setQueueName("node1.simple");
		producer.send(request, node);
	}

}
