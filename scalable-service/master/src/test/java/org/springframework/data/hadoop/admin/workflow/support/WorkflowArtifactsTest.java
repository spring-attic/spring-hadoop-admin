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

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;

/**
 * @author Jarred Li
 *
 */
public class WorkflowArtifactsTest {

	private WorkflowArtifacts artifacts;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		artifacts = new WorkflowArtifacts();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		artifacts = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts#getWorkflowDescriptor()}.
	 */
	@Test
	public void testGetWorkflowDescriptor() {
		Resource descriptor = artifacts.getWorkflowDescriptor();
		Assert.assertNull(descriptor);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts#setWorkflowDescriptor(org.springframework.core.io.Resource)}.
	 */
	@Test
	public void testSetWorkflowDescriptor() {
		FileSystemResource resource = new FileSystemResource(
				"src/test/resources/org/springframework/data/hadoop/admin/workflow/support/context.xml");
		artifacts.setWorkflowDescriptor(resource);
		Resource descriptor = artifacts.getWorkflowDescriptor();
		Assert.assertNotNull(descriptor);
		Assert.assertSame(resource, descriptor);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts#getWorkflowClassLoader()}.
	 */
	@Test
	public void testGetWorkflowClassLoader() {
		ClassLoader loader = artifacts.getWorkflowClassLoader();
		Assert.assertNull(loader);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts#setWorkflowClassLoader(java.lang.ClassLoader)}.
	 */
	@Test
	public void testSetWorkflowClassLoader() {
		File folder = new File("src/test/resources/org/springframework/data/hadoop/admin/workflow/support");
		ClassLoader loader = HadoopWorkflowUtils.getWorkflowClassLoader(folder);
		artifacts.setWorkflowClassLoader(loader);
		ClassLoader getLoader = artifacts.getWorkflowClassLoader();
		Assert.assertSame(loader, getLoader);

	}

}
