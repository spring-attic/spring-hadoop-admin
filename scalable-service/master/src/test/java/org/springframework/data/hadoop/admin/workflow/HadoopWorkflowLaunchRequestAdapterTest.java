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
import java.util.Collection;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Jarred Li
 *
 */
public class HadoopWorkflowLaunchRequestAdapterTest {

	private HadoopWorkflowLaunchRequestAdapter launchRequest;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		launchRequest = new HadoopWorkflowLaunchRequestAdapter();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		launchRequest = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.HadoopWorkflowLaunchRequestAdapter#handleUploadedFile(java.io.File)}.
	 */
	@Test
	public void testHandleUploadedFile() {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });
		launchRequest.setApplicationContext(context);

		File descriptor = new File("src/test/resources/org/springframework/data/hadoop/admin/workflow/context.xml");
		launchRequest.handleUploadedFile(descriptor);

		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		Collection<String> jobNames = jobRegistry.getJobNames();
		Assert.assertEquals(1, jobNames.size());
		Assert.assertTrue(jobNames.contains("wordcount-withscript-job"));
	}

}
