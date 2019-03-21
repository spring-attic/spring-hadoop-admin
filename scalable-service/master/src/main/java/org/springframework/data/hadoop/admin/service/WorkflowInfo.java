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
package org.springframework.data.hadoop.admin.service;

import java.util.List;

/**
 * @author Jarred Li
 *
 */
public class WorkflowInfo {
	
	private String workflowName;

	private String jobName = "NULL";
	
	private boolean valid;
	
	private boolean registered;
	
	private List<FileInfo> files;
	
	/**
	 * @return the workflowName
	 */
	public String getWorkflowName() {
		return workflowName;
	}

	/**
	 * @param workflowName the workflowName to set
	 */
	public void setWorkflowName(String workflowName) {
		this.workflowName = workflowName;
	}

	/**
	 * @return the jobName
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @param jobName the jobName to set
	 */
	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	/**
	 * @return the valid
	 */
	public boolean isValid() {
		return valid;
	}

	/**
	 * @param valid the valid to set
	 */
	public void setValid(boolean valid) {
		this.valid = valid;
	}

	/**
	 * @return the registered
	 */
	public boolean isRegistered() {
		return registered;
	}

	/**
	 * @param egistered the registered to set
	 */
	public void setRegistered(boolean registered) {
		this.registered = registered;
	}

	/**
	 * @return the files
	 */
	public List<FileInfo> getFiles() {
		return files;
	}

	/**
	 * @param files the files to set
	 */
	public void setFiles(List<FileInfo> files) {
		this.files = files;
	}
	
}
