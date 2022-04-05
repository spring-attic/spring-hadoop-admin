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
package org.springframework.data.hadoop.admin.workflow.support;

import java.io.File;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.hadoop.admin.SpringHadoopAdminWorkflowException;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;

/**
 * @author Jarred Li
 *
 */
public class FileSystemApplicationContextFactoryTest {

	private static final Log logger = LogFactory.getLog(FileSystemApplicationContextFactoryTest.class);

	private FileSystemApplicationContextFactory contextFactory;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		contextFactory = new FileSystemApplicationContextFactory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		contextFactory = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextFactory#createApplicationContext()}.
	 * @throws SpringHadoopAdminWorkflowException 
	 * @throws ConfigurationException 
	 */
	@Test
	public void testCreateApplicationContext() throws ConfigurationException, SpringHadoopAdminWorkflowException {
		ApplicationContext rootContext = null;
		try {
			rootContext = new ClassPathXmlApplicationContext(new String[] {
					"classpath:org/springframework/data/hadoop/admin/env-context.xml",
					"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
					"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });
		} catch (Exception e) {
			Assert.fail("test create root application context failed." + e.getMessage());
		}
		try {
			JobRepository jobRepository = rootContext.getBean("jobRepository", JobRepository.class);
			if (jobRepository == null) {
				Assert.fail("test get JobRepository is null");
			}
		} catch (Exception e) {
			Assert.fail("test get JobRepository failed." + e.getMessage());
		}
		contextFactory.setApplicationContext(rootContext);
		File descriptor = new File("src/test/resources/org/springframework/data/hadoop/admin/workflow/support/context.xml");
		File folder = descriptor.getParentFile();
		HadoopWorkflowUtils.processUploadedFile(folder);
		ClassLoader loader = HadoopWorkflowUtils.getWorkflowClassLoader(folder);
		contextFactory.setBeanClassLoader(loader);
		contextFactory.setResource(new FileSystemResource(
				"src/test/resources/org/springframework/data/hadoop/admin/workflow/support/context.xml"));
		ConfigurableApplicationContext newContext = contextFactory.createApplicationContext();
		logger.info("new context:" + newContext.toString());
		Object obj = newContext.getBean("wordcount-withscript-job");
		Assert.assertNotNull(obj);
	}


	@Test(expected = NoSuchBeanDefinitionException.class)
	public void testCreateApplicationContext_withourResource() {
		ApplicationContext rootContext = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });
		contextFactory.setApplicationContext(rootContext);
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/workflow/support");
		ClassLoader loader = HadoopWorkflowUtils.getWorkflowClassLoader(folder);
		contextFactory.setBeanClassLoader(loader);
		ConfigurableApplicationContext newContext = contextFactory.createApplicationContext();
		Object obj = newContext.getBean("wordcount-withscript-job");
		Assert.assertNull(obj);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextFactory#getBeanClassLoader()}.
	 */
	@Test
	public void testGetBeanClassLoader() {
		ClassLoader loader = contextFactory.getBeanClassLoader();
		Assert.assertNull(loader);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextFactory#setBeanClassLoader(java.lang.ClassLoader)}.
	 */
	@Test
	public void testSetBeanClassLoader() {
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		contextFactory.setBeanClassLoader(threadClassLoader);
		ClassLoader loader = contextFactory.getBeanClassLoader();
		Assert.assertSame(threadClassLoader, loader);
	}

}
