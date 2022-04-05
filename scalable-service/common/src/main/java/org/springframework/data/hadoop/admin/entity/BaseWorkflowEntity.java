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
package org.springframework.data.hadoop.admin.entity;

import java.io.Serializable;

import org.springframework.core.io.Resource;

/**
 * @author Jarred Li
 *
 */
public class BaseWorkflowEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2228483633653338928L;

	private Resource[] jars;	

	private Resource descriptor;
	
	private Resource propertyFile;

	private String name;

	/**
	 * @return the jars
	 */
	public Resource[] getJars() {
		return jars;
	}

	/**
	 * @param jars the jars to set
	 */
	public void setJars(Resource[] jars) {
		this.jars = jars;
	}

	/**
	 * @return the descriptor
	 */
	public Resource getDescriptor() {
		return descriptor;
	}

	/**
	 * @param descriptor the descriptor to set
	 */
	public void setDescriptor(Resource descriptor) {
		this.descriptor = descriptor;
	}

	/**
	 * @return the propertyFile
	 */
	public Resource getPropertyFile() {
		return propertyFile;
	}

	/**
	 * @param propertyFile the propertyFile to set
	 */
	public void setPropertyFile(Resource propertyFile) {
		this.propertyFile = propertyFile;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
}
