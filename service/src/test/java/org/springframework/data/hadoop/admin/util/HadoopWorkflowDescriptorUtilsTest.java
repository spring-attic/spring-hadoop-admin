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
package org.springframework.data.hadoop.admin.util;

import static org.junit.Assert.fail;

import java.io.File;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.data.hadoop.admin.SpringHadoopAdminWorkflowException;
import org.springframework.data.hadoop.util.PathUtils;

/**
 * @author Jarred Li
 *
 */
public class HadoopWorkflowDescriptorUtilsTest {

	private static final Log logger = LogFactory.getLog(HadoopWorkflowDescriptorUtilsTest.class);

	private HadoopWorkflowDescriptorUtils util;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUpBeforeClass() throws Exception {
		util = new HadoopWorkflowDescriptorUtils();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDownAfterClass() throws Exception {
		util = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.util.HadoopWorkflowDescriptorUtils#replacePropertyPlaceHolder(java.io.File)}.
	 */
	@Test
	public void testReplacePropertyPlaceHolder() {
		String fileName = "src/test/resources/context.xml";
		String properFilename = "src/test/resources/hadoop.properties";
		File f = new File(fileName);
		File propertiesFile = new File(properFilename);
		logger.debug("path:" + f.getParentFile().getAbsolutePath());
		if (f != null) {
			try {
				util.replacePropertyPlaceHolder(f, propertiesFile);
			} catch (SpringHadoopAdminWorkflowException e) {
				fail("exception was thrown when replacing proeprty place holder.");
			}
		}
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(fileName);
		String[] names = ctx.getBeanNamesForType(PathUtils.class);
		Assert.assertEquals(1, names.length);
	}

	@Test
	public void testReplaceJarPath() {
		File f = new File("src/test/resources/hadoop.properties");
		try {
			util.replaceJarPath(f);
		} catch (ConfigurationException e) {
			fail("exception was thrown when replacing jar path.");
		}
		try {
			PropertiesConfiguration config = new PropertiesConfiguration("src/test/resources/hadoop.properties");
			String newValue = config.getString("jar.path");
			Assert.assertNotNull(newValue, "jar.path should not be null");
		} catch (ConfigurationException e) {
			fail("read replaced property fail");
		}
	}

}
