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
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.data.hadoop.admin.SpringHadoopAdminWorkflowException;

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
	public void setUp() throws Exception {
		util = new HadoopWorkflowDescriptorUtils();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		util = null;
	}


	@Test
	public void testReplacePropertyPlaceHolder() {
		String fileName = "src/test/resources/org/springframework/data/hadoop/admin/util/context.xml";
		String properFilename = "src/test/resources/org/springframework/data/hadoop/admin/util/hadoop.properties";
		File f = new File(fileName);
		File propertiesFile = new File(properFilename);
		logger.debug("path:" + f.getParentFile().getAbsolutePath());
		if (f != null) {
			try {
				util.replacePropertyPlaceHolder(f, propertiesFile);
			} catch (SpringHadoopAdminWorkflowException e) {
				fail("exception was thrown when replacing proeprty place holder." + e.getMessage());
			}
		}
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(fileName);
		SimpleBean bean = ctx.getBean("simpleBean", SimpleBean.class);
		Assert.assertEquals("file://src/test/resources/org/springframework/data/hadoop/admin/util/hadoop-examples-1.0.0.jar",
				bean.getPath());
	}

	@Test(expected = SpringHadoopAdminWorkflowException.class)
	public void testReplacePropertyPlaceHolder_throwException() throws SpringHadoopAdminWorkflowException {
		String fileName = "src/test/resources/org/springframework/data/hadoop/admin/util/invalid-context.xml";
		String properFilename = "src/test/resources/org/springframework/data/hadoop/admin/util/hadoop.properties";
		File f = new File(fileName);
		File propertiesFile = new File(properFilename);
		logger.debug("path:" + f.getParentFile().getAbsolutePath());
		if (f != null) {
			util.replacePropertyPlaceHolder(f, propertiesFile);
		}
	}

	@Test
	public void testReplaceJarPath() {
		File f = new File("src/test/resources/org/springframework/data/hadoop/admin/util/hadoop.properties");
		try {
			util.replaceJarPath(f);
		} catch (ConfigurationException e) {
			fail("exception was thrown when replacing jar path.");
		}
		try {
			PropertiesConfiguration config = new PropertiesConfiguration(
					"src/test/resources/org/springframework/data/hadoop/admin/util/hadoop.properties");
			String newValue = config.getString("jar.path");
			Assert.assertNotNull(newValue, "jar.path should not be null");
		} catch (ConfigurationException e) {
			fail("read replaced property fail");
		}
	}

	@Test
	public void testClassLoader() {
		ClassLoader threadClassLoader = Thread.currentThread().getContextClassLoader();
		util.setBeanClassLoader(threadClassLoader);
		ClassLoader loader = util.getBeanClassLoader();
		Assert.assertNotNull(loader);
		Assert.assertEquals(threadClassLoader, loader);
	}

	@Test
	public void testResoureLoader() {
		ResourceLoader resourceLoader = new DefaultResourceLoader();
		util.setResourceLoader(resourceLoader);
		ResourceLoader loader = util.getResourceLoader();
		Assert.assertNotNull(loader);
		Assert.assertEquals(resourceLoader, loader);
	}
}
