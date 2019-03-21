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
package org.springframework.data.hadoop.admin.cli.util;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

/**
 * @author Jarred Li
 *
 */
public class PropertyUtilTest{


	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.util.PropertyUtil#setTargetUrl(java.lang.String)}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testSetTargetUrl() throws ConfigurationException {
		PropertyUtil.setTargetUrl("http://localhost:8080/spring-hadoop-admin");
		String url = PropertyUtil.getTargetUrl();
		Assert.assertNotNull(url);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.util.PropertyUtil#getTargetUrl()}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testGetTargetUrl() throws ConfigurationException {
		String value = "http://localhost:8080/spring-hadoop-admin";
		PropertyUtil.setTargetUrl(value);
		String url = PropertyUtil.getTargetUrl();
		Assert.assertEquals(value, url);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.util.PropertyUtil#setDfsName(java.lang.String)}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testSetDfsName() throws ConfigurationException {
		PropertyUtil.setDfsName("hdfs://localhost:9000");
		String url = PropertyUtil.getDfsName();
		Assert.assertNotNull(url);
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.util.PropertyUtil#getDfsName()}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testGetDfsName() throws ConfigurationException {
		String value = "hdfs://localhost:9000";
		PropertyUtil.setDfsName(value);
		String url = PropertyUtil.getDfsName();
		Assert.assertEquals(value, url);
	}

}
