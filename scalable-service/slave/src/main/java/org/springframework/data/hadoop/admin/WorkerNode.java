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
package org.springframework.data.hadoop.admin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.data.hadoop.admin.entity.WorkflowExecution;
import org.springframework.data.hadoop.admin.entity.WorkflowLaunchRequest;
import org.springframework.data.hadoop.admin.launch.LaunchWorkflow;
import org.springframework.data.hadoop.admin.message.MessageConsumer;

/**
 * @author Jarred Li
 *
 */
public class WorkerNode {

	private static final Log logger = LogFactory.getLog(WorkerNode.class);

	private static AnnotationAwareOrderComparator annocationOrderComparator = new AnnotationAwareOrderComparator();

	private ApplicationContext applicationContext;

	private boolean stop;

	private MessageConsumer<WorkflowLaunchRequest> messageConsumer;

	private LaunchWorkflow launcher;

	public WorkerNode() {
		this.applicationContext = new ClassPathXmlApplicationContext("META-INF/spring/hadoop-admin/worker-context.xml");
		messageConsumer = new MessageConsumer<WorkflowLaunchRequest>();
		launcher = getHighestPriorityLauncher(LaunchWorkflow.class);
		stop = false;
	}

	public void start() throws WorkflowLaunchException {
		while (!stop) {
			WorkflowLaunchRequest request = messageConsumer.receive();
			WorkflowExecution execution = launcher.launch(request);
			//TODO: handle execution here.
		}
	}

	public void stop() {
		stop = true;
	}

	public <T> T getHighestPriorityLauncher(Class<T> t) {
		Map<String, T> providers = BeanFactoryUtils.beansOfTypeIncludingAncestors(this.applicationContext, t);
		List<T> sortedProviders = new ArrayList<T>(providers.values());
		Collections.sort(sortedProviders, annocationOrderComparator);
		T highestPriorityProvider = sortedProviders.get(0);
		return highestPriorityProvider;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		WorkerNode wn = new WorkerNode();
		try {
			wn.start();
		} catch (WorkflowLaunchException e) {
			logger.fatal("launch workflow failed", e);
		}
	}
}
