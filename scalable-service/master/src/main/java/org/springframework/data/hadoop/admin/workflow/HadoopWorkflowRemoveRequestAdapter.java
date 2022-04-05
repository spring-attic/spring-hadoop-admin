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
package org.springframework.data.hadoop.admin.workflow;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;
import org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextFactory;
import org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * Spring Integration Endpoint to handle removed files
 * If the removed file is wokflow desciptor (.xml), the registered job will be unregisted.
 * 
 * @author Jarred Li
 *
 */
@MessageEndpoint
public class HadoopWorkflowRemoveRequestAdapter implements ApplicationContextAware {

	private static final Log logger = LogFactory.getLog(HadoopWorkflowRemoveRequestAdapter.class);

	private ApplicationContext context;

	/**
	 * Handle the removed file. If the removed file is workflow decriptor, unregister job.
	 * 
	 * @param file The file to be removed.
	 */
	@ServiceActivator
	public void handleRemoveFile(File file) {
		logger.info("remove file:" + file.getAbsolutePath());
		if (file == null || !file.exists()) {
			return;
		}

		try {
			WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(file.getParentFile());
			if (artifacts != null) {
				FileSystemApplicationContextFactory factory = new FileSystemApplicationContextFactory();
				factory.setApplicationContext(context);
				factory.setBeanClassLoader(artifacts.getWorkflowClassLoader());
				factory.setResource(artifacts.getWorkflowDescriptor());
				ConfigurableApplicationContext appContext = factory.createApplicationContext();
				Map<String, Job> springBatchJobs = appContext.getBeansOfType(Job.class);

				if (springBatchJobs.size() > 0) {
					for (String jobName : springBatchJobs.keySet()) {
						HadoopWorkflowUtils.unregisterSpringBatchJob(context, jobName);
					}
				}
				else {
					String jobName = HadoopWorkflowUtils.generateSpringBatchJobName(artifacts);
					HadoopWorkflowUtils.unregisterSpringBatchJob(context, jobName);
				}
			}
		} catch (Exception e) {
			logger.error("handle removed file failed", e);
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;

	}
}
