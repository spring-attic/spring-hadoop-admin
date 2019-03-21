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

import org.springframework.core.io.Resource;

/**
 * A pair of artifacts, <code>Resource</code> which is workflow descriptor and <code>ClassLoader</code>
 * which is used to load Beans in the descriptor.
 * 
 * @author Jarred Li
 *
 */
public class WorkflowArtifacts {

	private Resource workflowDescriptor;

	private ClassLoader workflowClassLoader;

	public WorkflowArtifacts() {

	}

	public WorkflowArtifacts(Resource descriptor, ClassLoader loader) {
		this.workflowDescriptor = descriptor;
		this.workflowClassLoader = loader;
	}

	/**
	 * @return the workflowDescriptor
	 */
	public Resource getWorkflowDescriptor() {
		return workflowDescriptor;
	}

	/**
	 * @param workflowDescriptor the workflowDescriptor to set
	 */
	public void setWorkflowDescriptor(Resource workflowDescriptor) {
		this.workflowDescriptor = workflowDescriptor;
	}

	/**
	 * @return the workflowClassLoader
	 */
	public ClassLoader getWorkflowClassLoader() {
		return workflowClassLoader;
	}

	/**
	 * @param workflowClassLoader the workflowClassLoader to set
	 */
	public void setWorkflowClassLoader(ClassLoader workflowClassLoader) {
		this.workflowClassLoader = workflowClassLoader;
	}


}
