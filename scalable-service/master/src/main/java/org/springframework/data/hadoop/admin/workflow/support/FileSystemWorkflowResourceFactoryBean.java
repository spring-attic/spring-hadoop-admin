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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.hadoop.admin.util.Constant;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;
import org.springframework.util.Assert;

/**
 * Factory bean to create list of <code>WorkflowArtifacts</code> from the specified folder:
 * <code>getBaseWorkflowDescriptorDir</code>
 * 
 * @author Jarred Li
 *
 */
public class FileSystemWorkflowResourceFactoryBean implements FactoryBean<Object>, InitializingBean {

	private static final Log logger = LogFactory.getLog(FileSystemWorkflowResourceFactoryBean.class);

	private WorkflowArtifacts[] workflowArtifacts;

	private String baseWorkflowDescriptorDir;

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	@Override
	public WorkflowArtifacts[] getObject() throws Exception {
		return workflowArtifacts;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	@Override
	public Class<?> getObjectType() {
		return WorkflowArtifacts[].class;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	@Override
	public boolean isSingleton() {
		return true;
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(this.baseWorkflowDescriptorDir, "must set base workflow descriptor directory");
		File parentDir = new File(System.getProperty("java.io.tmpdir", "/tmp"), Constant.WORKFLOW_LOCATION);
		List<WorkflowArtifacts> artifacts = new ArrayList<WorkflowArtifacts>();
		File baseDir = new File(parentDir, this.baseWorkflowDescriptorDir);
		if (baseDir.exists()) {
			logger.info("workflow descriptor base directory:" + baseDir.getAbsolutePath());
			File[] wfDescriptors = baseDir.listFiles();
			for (File f : wfDescriptors) {
				if (f.isDirectory()) {
					String[] descriptors = HadoopWorkflowUtils.getWorkflowDescriptor(f);
					ClassLoader loader = HadoopWorkflowUtils.getWorkflowClassLoader(f);
					logger.info("::afterPropertiesSet, class loader:" + loader);
					if (descriptors != null && descriptors.length > 0 && loader != null) {
						WorkflowArtifacts art = new WorkflowArtifacts(new FileSystemResource(descriptors[0]), loader);
						artifacts.add(art);
					}
				}
			}
			workflowArtifacts = artifacts.toArray(new WorkflowArtifacts[0]);
		}
	}

	/**
	 * @return the baseWorkflowDescriptorDir
	 */
	public String getBaseWorkflowDescriptorDir() {
		return baseWorkflowDescriptorDir;
	}

	/**
	 * @param baseWorkflowDescriptorDir the baseWorkflowDescriptorDir to set
	 */
	public void setBaseWorkflowDescriptorDir(String baseWorkflowDescriptorDir) {
		this.baseWorkflowDescriptorDir = baseWorkflowDescriptorDir;
	}

}
