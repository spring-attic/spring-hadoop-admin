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
package org.springframework.data.hadoop.admin.util;


import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.admin.SpringHadoopAdminWorkflowException;
import org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts;


/**
 * @author Jarred Li
 *
 */
public class HadoopWorkflowUtilsTest {

	private static final Log logger = LogFactory.getLog(HadoopWorkflowUtilsTest.class);

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#getWorkflowArtifacts(java.io.File)}.
	 */
	@Test
	public void testGetWorkflowArtifacts() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/test");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		Assert.assertNotNull(artifacts);
	}

	@Test
	public void testGetWorkflowArtifacts_withoutFiles() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutFiles");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		Assert.assertNull(artifacts);
	}

	@Test
	public void testGetWorkflowArtifacts_withoutDescriptor() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutDescriptor");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		Assert.assertNull(artifacts);
	}

	@Test
	public void testGetWorkflowArtifacts_withoutProperties() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutProperties");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		Assert.assertNull(artifacts);
	}

	@Test
	public void testGetWorkflowArtifacts_withoutJars() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutJars");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		Assert.assertNull(artifacts);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#getWorkflowDescriptor(java.io.File)}.
	 */
	@Test
	public void testGetWorkflowDescriptorFile() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/test");
		String[] descriptors = HadoopWorkflowUtils.getWorkflowDescriptor(folder);
		Assert.assertNotNull(descriptors);
	}

	@Test
	public void testGetWorkflowDescriptorFile_wihtoutDescriptor() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutDescriptor");
		String[] descriptors = HadoopWorkflowUtils.getWorkflowDescriptor(folder);
		Assert.assertNull(descriptors);
	}

	@Test
	public void testGetWorkflowDescriptorFile_withoutProperties() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutProperties");
		String[] descriptors = HadoopWorkflowUtils.getWorkflowDescriptor(folder);
		Assert.assertNull(descriptors);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#getWorkflowLibararies(java.io.File)}.
	 */
	@Test
	public void testGetWorkflowLibararies() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/test");
		URL urls[] = HadoopWorkflowUtils.getWorkflowLibararies(folder);
		Assert.assertNotNull(urls);
	}

	@Test
	public void testGetWorkflowLibararies_withoutJars() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutJars");
		URL urls[] = HadoopWorkflowUtils.getWorkflowLibararies(folder);
		Assert.assertNull(urls);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#getWorkflowClassLoader(java.io.File)}.
	 */
	@Test
	public void testGetWorkflowClassLoader() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/test");
		ClassLoader loader = HadoopWorkflowUtils.getWorkflowClassLoader(folder);
		Assert.assertNotNull(loader);
		boolean isURLClassLoader = false;
		if (loader instanceof URLClassLoader) {
			isURLClassLoader = true;
		}
		Assert.assertTrue(isURLClassLoader);

		ClassLoader parent = HadoopWorkflowUtils.class.getClassLoader();
		Assert.assertNotSame(parent, loader);
	}

	@Test
	public void testGetWorkflowClassLoader_withoutJars() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutJars");
		ClassLoader loader = HadoopWorkflowUtils.getWorkflowClassLoader(folder);
		Assert.assertNotNull(loader);
		ClassLoader parent = HadoopWorkflowUtils.class.getClassLoader();
		Assert.assertSame(parent, loader);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#isSpringBatchJob(org.springframework.context.ApplicationContext, org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts)}.
	 * @throws SpringHadoopAdminWorkflowException 
	 */
	@Test
	public void testIsSpringBatchJob() throws SpringHadoopAdminWorkflowException {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/util/test");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);

		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });
		boolean isSpringBatchJob = HadoopWorkflowUtils.isSpringBatchJob(context, artifacts);
		Assert.assertFalse(isSpringBatchJob);


	}

	@Test
	public void testIsSpringBatchJob_springBatchJob() throws Exception {
		File descriptor = new File(
				"src/test/resources/org/springframework/data/hadoop/admin/util/testSpringBatchJob/context.xml");
		File folder = descriptor.getParentFile();
		HadoopWorkflowUtils.processUploadedFile(folder);
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext(new String[] {
					"classpath:org/springframework/data/hadoop/admin/env-context.xml",
					"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
					"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });

			boolean isSpringBatchJob = HadoopWorkflowUtils.isSpringBatchJob(context, artifacts);
			logger.info("is spring batch job:" + isSpringBatchJob);
			Assert.assertTrue("this job should be Spring Batch Job", isSpringBatchJob);
		} catch (Exception e) {
			Assert.fail("test create root application context failed." + e.getMessage());
		}
		try {
			JobRepository jobRepository = context.getBean("jobRepository", JobRepository.class);
			if (jobRepository == null) {
				Assert.fail("test get JobRepository is null");
			}
		} catch (Exception e) {
			Assert.fail("test get JobRepository failed." + e.getMessage());
		}

	}


	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#createAndRegisterSpringBatchJob(org.springframework.context.ApplicationContext, org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts)}.
	 * @throws SpringHadoopAdminWorkflowException 
	 */
	@Test
	public void testCreateAndRegisterSpringBatchJob() throws SpringHadoopAdminWorkflowException {
		File folder = new File(
				"src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutSpringBatchJob");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });

		HadoopWorkflowUtils.createAndRegisterSpringBatchJob(context, artifacts);

		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		Collection<String> jobNames = jobRegistry.getJobNames();
		Assert.assertEquals(1, jobNames.size());
		Assert.assertTrue(jobNames.contains("spring-hadoop-job-context.xml"));

	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#generateSpringBatchJobName(org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts)}.
	 */
	@Test
	public void testGenerateSpringBatchJobName() {
		File folder = new File(
				"src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutSpringBatchJob");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		String name = HadoopWorkflowUtils.generateSpringBatchJobName(artifacts);
		Assert.assertNotNull(name);
		Assert.assertEquals("spring-hadoop-job-context.xml", name);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#unregisterSpringBatchJob(org.springframework.context.ApplicationContext, java.lang.String)}.
	 * @throws SpringHadoopAdminWorkflowException 
	 */
	@Test
	public void testUnregisterSpringBatchJob() throws SpringHadoopAdminWorkflowException {
		File folder = new File(
				"src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutSpringBatchJob");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });

		HadoopWorkflowUtils.createAndRegisterSpringBatchJob(context, artifacts);

		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		HadoopWorkflowUtils.unregisterSpringBatchJob(context, "spring-hadoop-job-context.xml");
		Collection<String> jobNames = jobRegistry.getJobNames();
		Assert.assertEquals(0, jobNames.size());


	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils#getWorkflowDescriptor(org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts)}.
	 * @throws SpringHadoopAdminWorkflowException 
	 */
	@Test
	public void testGetWorkflowDescriptorWorkflowArtifacts() throws SpringHadoopAdminWorkflowException {
		File folder = new File(
				"src/test/resources/org/springframework/data/hadoop/admin/util/testWithoutSpringBatchJob");
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(folder);

		String name = HadoopWorkflowUtils.getWorkflowDescriptor(artifacts);
		Assert.assertNotNull(name);

	}
	
	@Test
	public void testprocessAndRegisterWorkflow() {
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });

		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/workflow");
		HadoopWorkflowUtils.processAndRegisterWorkflow(folder, context);		

		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		Collection<String> jobNames = jobRegistry.getJobNames();
		Assert.assertEquals(1, jobNames.size());
		Assert.assertTrue(jobNames.contains("wordcount-withscript-job"));
	}


}
