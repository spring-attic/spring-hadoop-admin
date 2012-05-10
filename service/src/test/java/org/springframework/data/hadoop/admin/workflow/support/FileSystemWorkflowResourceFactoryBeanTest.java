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
package org.springframework.data.hadoop.admin.workflow.support;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.Resource;

/**
 * @author Jarred Li
 *
 */
public class FileSystemWorkflowResourceFactoryBeanTest {

	private FileSystemWorkflowResourceFactoryBean workflowResourceFactoryBean;
	
	private static final Log logger = LogFactory.getLog(FileSystemWorkflowResourceFactoryBeanTest.class);
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUpBeforeClass() throws Exception {
		workflowResourceFactoryBean = new FileSystemWorkflowResourceFactoryBean();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDownAfterClass() throws Exception {
		workflowResourceFactoryBean = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.workflow.support.FileSystemWorkflowResourceFactoryBean#getObject()}.
	 * @throws Exception 
	 */
	@Test
	public void testGetObject() throws Exception {
	}

}
