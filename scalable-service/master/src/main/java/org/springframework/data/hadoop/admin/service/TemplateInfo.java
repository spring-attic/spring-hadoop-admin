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

/**
 * @author Jarred Li
 *
 */
public class TemplateInfo{

	private String templateName;
	
	private String templateVersion;
	
	private FileInfo fileInfo;

	/**
	 * @return the fileInfo
	 */
	public FileInfo getFileInfo() {
		return fileInfo;
	}

	/**
	 * @param fileInfo the fileInfo to set
	 */
	public void setFileInfo(FileInfo fileInfo) {
		this.fileInfo = fileInfo;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the templateVersion
	 */
	public String getTemplateVersion() {
		return templateVersion;
	}

	/**
	 * @param templateVersion the templateVersion to set
	 */
	public void setTemplateVersion(String templateVersion) {
		this.templateVersion = templateVersion;
	}
	
}
