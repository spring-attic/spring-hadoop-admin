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
package org.springframework.data.hadoop.admin.cli.commands;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.hadoop.admin.cli.commands.WorkflowCommand;

/**
 * @author Jarred Li
 *
 */
public class FileCommandTest {
	
	private WorkflowCommand cmd;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cmd = new WorkflowCommand();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		cmd = null;
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.mapreduce.commands.WorkflowCommand#listFiles()}.
	 */
	@Test
	public void testListFiles() {
		//cmd.listFiles();
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.mapreduce.commands.WorkflowCommand#downloadFile(java.lang.String)}.
	 */
	@Test
	public void testDownloadFile() {
		
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.mapreduce.commands.WorkflowCommand#uploadFile(java.lang.String, java.lang.String)}.
	 */
	@Test
	public void testUploadFile() {
		
	}

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.cli.mapreduce.commands.WorkflowCommand#deleteFile(java.lang.String)}.
	 */
	@Test
	public void testDeleteFile() {
		//cmd.deleteFile("pig/hadoop.20120516.110125.properties");
	}

}
