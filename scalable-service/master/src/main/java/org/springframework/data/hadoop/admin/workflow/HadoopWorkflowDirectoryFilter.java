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

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

/**
 * Filter the uploaded file. If the file is uploaded to the specified directory, this file will be processed.
 * Otherwise, it will be ignored.
 * 
 * @author Jarred Li
 *
 */
public class HadoopWorkflowDirectoryFilter implements InitializingBean {

	private String springHadoopWorkflowDirectoryName;


	/**
	 * whether the file is uploaded to specified folder
	 * 
	 * @param file the file is uploaded
	 * 
	 * @return True - If the file is uploaded to specified folder
	 * 		   False - Otherwise
	 */
	public boolean filter(File file) {
		return file.getParentFile().getAbsolutePath().contains(springHadoopWorkflowDirectoryName);
	}


	/**
	 * @return the springHadoopWorkflowDirectoryName
	 */
	public String getSpringHadoopWorkflowDirectoryName() {
		return springHadoopWorkflowDirectoryName;
	}


	/**
	 * @param springHadoopWorkflowDirectoryName the springHadoopWorkflowDirectoryName to set
	 */
	public void setSpringHadoopWorkflowDirectoryName(String springHadoopWorkflowFolderName) {
		this.springHadoopWorkflowDirectoryName = springHadoopWorkflowFolderName;
	}


	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(springHadoopWorkflowDirectoryName, "spring hadoop workflow directory must be set");
	}

}
