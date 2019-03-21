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

/**
 * @author Jarred Li
 *
 */
public class HadoopWorkflowDirectoryTest {

	private HadoopWorkflowDirectory hadoopWorkflowDirectory;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		hadoopWorkflowDirectory = new HadoopWorkflowDirectory();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		hadoopWorkflowDirectory = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.HadoopWorkflowDirectory#getHadoopWorkflowDirectory()}.
	 */
	@Test
	public void testGetHadoopWorkflowDirectory() {
		String dir = hadoopWorkflowDirectory.getHadoopWorkflowDirectory();
		Assert.assertNull(dir);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.HadoopWorkflowDirectory#setHadoopWorkflowDirectory(java.lang.String)}.
	 */
	@Test
	public void testSetHadoopWorkflowDirectory() {
		String setValue = "test";
		hadoopWorkflowDirectory.setHadoopWorkflowDirectory(setValue);
		String dir = hadoopWorkflowDirectory.getHadoopWorkflowDirectory();
		Assert.assertEquals(setValue, dir);
	}

}
