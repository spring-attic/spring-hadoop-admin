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

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.hadoop.admin.util.Constant;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;

import com.google.common.io.Files;

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
		File parentDir = new File(System.getProperty("java.io.tmpdir", "/tmp"), Constant.WORKFLOW_LOCATION);
		logger.info("parent dir:" + parentDir.getAbsolutePath());
		File target = new File(parentDir, "wordcount");
		if (target.exists()) {
			target.delete();
		}
		Files.createParentDirs(target);
		File file = new File("src/test/resources/org/springframework/data/hadoop/admin/workflow/support/copy");
		File to = null;
		for (File f : file.listFiles()) {
			to = new File(target, f.getName());
			Files.createParentDirs(to);
			to.createNewFile();
			Files.copy(f, to);
		}

		File folder = to.getParentFile();
		HadoopWorkflowUtils.processUploadedFile(folder);

		
		workflowResourceFactoryBean.setBaseWorkflowDescriptorDir("");
		workflowResourceFactoryBean.afterPropertiesSet();
		WorkflowArtifacts[] artifacts = workflowResourceFactoryBean.getObject();
		Assert.assertEquals(1, artifacts.length);
	}

}
