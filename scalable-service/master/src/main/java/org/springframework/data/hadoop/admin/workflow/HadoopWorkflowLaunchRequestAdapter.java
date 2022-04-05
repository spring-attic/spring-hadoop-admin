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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

/**
 * Spring Integration Endpoint to handle uploaded file to create Spring Batch Job on the fly.
 * To create a new Job, 3 kinds of files must be uploaded - jar, xml, and properties.
 * 
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
		File parentFolder = workflowFile.getParentFile();
		HadoopWorkflowUtils.processAndRegisterWorkflow(parentFolder, context);
	}



}
