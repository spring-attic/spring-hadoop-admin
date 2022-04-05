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

import java.io.IOException;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.hadoop.admin.cli.commands.ProjectCommand;

/**
 * @author Jarred Li
 *
 */
public class ProjectCommandTest {
	
	ProjectCommand cmd;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		cmd = new ProjectCommand();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		cmd = null;
	}


	@Test
	public void testJSON() throws JsonGenerationException, JsonMappingException, IOException{
		ProjectCommand.TemplateList templateList = new ProjectCommand.TemplateList();
		ProjectCommand.TemplateList.Templates templates = new ProjectCommand.TemplateList.Templates();
		ProjectCommand.TemplateList.Templates.Uploaded upload1 = new ProjectCommand.TemplateList.Templates.Uploaded();
		upload1.setName("MapReduce");
		upload1.setPath("MapReduce.zip");
		ProjectCommand.TemplateList.Templates.Uploaded upload2 = new ProjectCommand.TemplateList.Templates.Uploaded();
		upload2.setName("HDFS");
		upload2.setPath("HDFS.zip");
		templates.setUploaded(new ProjectCommand.TemplateList.Templates.Uploaded[]{upload1, upload2});
		templateList.setTemplates(templates);
		ObjectMapper mapper = new ObjectMapper();		
		String json = mapper.writeValueAsString(templateList);
		System.out.println(json);
	}
}
