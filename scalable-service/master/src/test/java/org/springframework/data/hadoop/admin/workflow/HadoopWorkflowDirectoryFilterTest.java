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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jarred Li
 *
 */
public class HadoopWorkflowDirectoryFilterTest {

	private HadoopWorkflowDirectoryFilter filter;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		filter = new HadoopWorkflowDirectoryFilter();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		filter = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.HadoopWorkflowDirectoryFilter#filter(java.io.File)}.
	 */
	@Test
	public void testFilter() {
		String value = "test";
		filter.setSpringHadoopWorkflowDirectoryName(value);
		File file = new File("src/test/resources/org/springframework/data/hadoop/admin/util/test/context.xml");
		boolean filtered = filter.filter(file);
		Assert.assertTrue(filtered);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.HadoopWorkflowDirectoryFilter#getSpringHadoopWorkflowDirectoryName()}.
	 */
	@Test
	public void testGetSpringHadoopWorkflowDirectoryName() {
		String dir = filter.getSpringHadoopWorkflowDirectoryName();
		Assert.assertNull(dir);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.HadoopWorkflowDirectoryFilter#setSpringHadoopWorkflowDirectoryName(java.lang.String)}.
	 */
	@Test
	public void testSetSpringHadoopWorkflowDirectoryName() {
		String value = "test";
		filter.setSpringHadoopWorkflowDirectoryName(value);
		String dir = filter.getSpringHadoopWorkflowDirectoryName();
		Assert.assertEquals(value, dir);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.HadoopWorkflowDirectoryFilter#afterPropertiesSet()}.
	 * @throws Exception 
	 */
	@Test(expected = Exception.class)
	public void testAfterPropertiesSet() throws Exception {
		filter.afterPropertiesSet();
	}

	@Test
	public void testAfterPropertiesSet_setProperty() {
		filter.setSpringHadoopWorkflowDirectoryName("test");
		try {
			filter.afterPropertiesSet();
		} catch (Exception e) {
			Assert.fail("exception was thrown");
		}
	}

}
