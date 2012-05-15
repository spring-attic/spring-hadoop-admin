/*
 * Copyright 2011-2012 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.data.hadoop.admin.workflow;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.AutomaticJobRegistrar;
import org.springframework.batch.core.configuration.support.DefaultJobLoader;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.admin.SpringHadoopAdminWorkflowException;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowDescriptorUtils;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;
import org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextsFactoryBean;
import org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;



/**
 * Handle uploaded file to specified location which is for Spring Hadoop. 
 * If all the Spring Hadoop artifacts (jar, workflow descriptor, properties)
 * are uploaded, a new Spring Batch job will be registered.  
 * 
 * @author Jarred Li
 *
 */

@MessageEndpoint
public class HadoopWorkflowLaunchRequestAdapter implements ApplicationContextAware {

	private static final Log logger = LogFactory.getLog(HadoopWorkflowLaunchRequestAdapter.class);

	private ApplicationContext context;


	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.context = applicationContext;
	}


	/**
	 * handle spring hadoop workflow after jar, descriptor and properties are uploaded to server.
	 * 
	 * @param workflowFile the uploaded file.
	 * 
	 */
	@ServiceActivator
	public void handleUploadedFile(final File workflowFile) {
		logger.info("upload file:" + workflowFile.getAbsolutePath());
		try {
			WorkflowArtifacts artifacts = processUploadedFile(workflowFile);
			if (artifacts == null) {
				return;
			}

			boolean isSpringBatchJob = HadoopWorkflowUtils.isSpringBatchJob(context, artifacts);
			logger.info("uploaded workflow artifacts is spring batch job? " + isSpringBatchJob);
			if (isSpringBatchJob) {
				loadSpringBatchJob(artifacts);
			}
			else {
				HadoopWorkflowUtils.createAndRegisterSpringBatchJob(context, artifacts);
			}
		} catch (Exception e) {
			logger.error("handle uploaded file failed", e);
		}

	}

	/**
	 * process the uploaded file. Replace context place holder locaton, update jar path.
	 * 
	 * @param workflowFile workflow files that are being uploaded
	 * @return <code>WorkflowArtifacts<code> when all jar, descriptor, properties are uploaded
	 * 			otherwise, null 
	 * @throws ConfigurationException 
	 * @throws SpringHadoopAdminWorkflowException 
	 */
	public WorkflowArtifacts processUploadedFile(final File workflowFile) throws ConfigurationException,
			SpringHadoopAdminWorkflowException {
		File parentFolder = workflowFile.getParentFile();
		String[] descriptor = HadoopWorkflowUtils.getWorkflowDescriptor(parentFolder);
		URL[] urls = HadoopWorkflowUtils.getWorkflowLibararies(parentFolder);
		if (descriptor == null || urls == null || urls.length == 0) {
			return null;
		}

		ClassLoader parentLoader = HadoopWorkflowLaunchRequestAdapter.class.getClassLoader();
		ClassLoader loader = new URLClassLoader(urls, parentLoader);

		String workflowDescriptorFileName = descriptor[0];
		String workflowPropertyFileName = descriptor[1];
		File workflowPropertyFile = new File(workflowPropertyFileName);
		File workflowDescriptorFile = new File(workflowDescriptorFileName);
		HadoopWorkflowDescriptorUtils util = new HadoopWorkflowDescriptorUtils();
		util.setBeanClassLoader(loader);

		util.replaceJarPath(workflowPropertyFile);
		boolean replaced = util.replacePropertyPlaceHolder(workflowDescriptorFile, workflowPropertyFile);

		if (!replaced) {
			throw new SpringHadoopAdminWorkflowException(
					"there is no property place holder in the workflow descriptor. MapReduce jar may not be found");
		}

		Resource resource = new FileSystemResource(new File(workflowDescriptorFileName));
		WorkflowArtifacts artifacts = new WorkflowArtifacts(resource, loader);
		return artifacts;
	}

	/**
	 * load the new uploaded Spring Batch job 
	 * 
	 * @param artifacts workflow artifacts
	 * 
	 * @throws Exception
	 */
	private void loadSpringBatchJob(WorkflowArtifacts artifacts) throws Exception {
		String workflowDescriptor = HadoopWorkflowUtils.getWorkflowDescriptor(artifacts);
		logger.info("create spring batch job:" + workflowDescriptor + ", classloader:"
				+ artifacts.getWorkflowClassLoader());
		AutomaticJobRegistrar registarar = new AutomaticJobRegistrar();
		FileSystemApplicationContextsFactoryBean factoryBean = new FileSystemApplicationContextsFactoryBean();
		factoryBean.setApplicationContext(context);
		factoryBean.setWorkflowArtifacts(new WorkflowArtifacts[] { artifacts });
		registarar.setApplicationContextFactories((ApplicationContextFactory[]) factoryBean.getObject());
		DefaultJobLoader jobLoader = new DefaultJobLoader();
		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		jobLoader.setJobRegistry(jobRegistry);
		registarar.setJobLoader(jobLoader);
		registarar.start();
		logger.info("successfully load spring batch job");
	}

}
