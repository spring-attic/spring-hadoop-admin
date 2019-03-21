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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Jarred Li
 *
 */
public class SimpleSpringHadoopTaskletTest {

	private SimpleSpringHadoopTasklet tasklet;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		tasklet = new SimpleSpringHadoopTasklet();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		tasklet = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.SimpleSpringHadoopTasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)}.
	 */
	@Test
	public void testExecute() {
		ApplicationContext rootContext = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });

		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
				new String[] { "src/test/resources/org/springframework/data/hadoop/admin/workflow/context.xml" },
				false, rootContext);
		tasklet.setContext(context);
		try {
			tasklet.execute(null, null);
		} catch (Exception e) {
			Assert.fail("run test with exception");
		}
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.SimpleSpringHadoopTasklet#getContext()}.
	 */
	@Test
	public void testGetContext() {
		FileSystemXmlApplicationContext obj = tasklet.getContext();
		Assert.assertNull(obj);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.SimpleSpringHadoopTasklet#setContext(org.springframework.context.support.FileSystemXmlApplicationContext)}.
	 */
	@Test
	public void testSetContext() {
		ApplicationContext rootContext = new ClassPathXmlApplicationContext(new String[] {
				"classpath:org/springframework/data/hadoop/admin/env-context.xml",
				"classpath:org/springframework/data/hadoop/admin/data-source-context.xml",
				"classpath:org/springframework/data/hadoop/admin/execution-context.xml", });

		FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
				new String[] { "src/test/resources/org/springframework/data/hadoop/admin/workflow/context.xml" },
				false, rootContext);
		tasklet.setContext(context);
		Assert.assertSame(context, tasklet.getContext());
	}

}
