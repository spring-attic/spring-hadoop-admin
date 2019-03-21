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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.hadoop.admin.util.Constant;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;
import org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextFactory;
import org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts;

/**
 * @author Jarred Li
 *
 */
public class SimpleWorkflowService extends BaseFileService implements WorkflowService, ApplicationContextAware {
	
	{
		File outputDir = new File(System.getProperty("java.io.tmpdir", "/tmp"), Constant.WORKFLOW_LOCATION);
		setOutputDir(outputDir);
	}
	
	private static final Log logger = LogFactory.getLog(SimpleWorkflowService.class);

	private ApplicationContext context;

	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.context = arg0;
	}

	/* (non-Javadoc)
	 * @see org.springframework.data.hadoop.admin.service.WorkflowService#processAndRegister(java.lang.String)
	 */
	@Override
	public void processAndRegister(String path) {
		File folder = new File("/" + getOutputDir().getAbsolutePath() + "/" + path);
		HadoopWorkflowUtils.processAndRegisterWorkflow(folder, context);
	}



	public List<WorkflowInfo> getWorkflows(int start, int pageSize) throws IOException {
		List<WorkflowInfo> wfInfoes = new ArrayList<WorkflowInfo>();
		if (getOutputDir().exists()) {
			logger.info("getWorkflows::workflow descriptor base directory:" + getOutputDir().getAbsolutePath());
			File[] wfArtifactsDirs = getOutputDir().listFiles();
			for (File f : wfArtifactsDirs) {
				if (f.isDirectory()) {
					List<FileInfo> files = getFiles(f.getName() + "/**");
					if (files.size() > 0) {
						WorkflowInfo wfInfo = new WorkflowInfo();
						wfInfo.setWorkflowName(f.getName());
						wfInfo.setFiles(files);
						boolean isValid = HadoopWorkflowUtils.isValidWorkflow(files);
						wfInfo.setValid(isValid);
						String jobName = getRegisteredJobName(f);
						boolean isRegistered = jobName != null;
						wfInfo.setRegistered(isRegistered);
						if (isRegistered) {
							wfInfo.setJobName(jobName);
						}
						wfInfoes.add(wfInfo);
					}
				}
			}

		}
		return new ArrayList<WorkflowInfo>(wfInfoes.subList(start, Math.min(start + pageSize, wfInfoes.size())));
	}

	private String getRegisteredJobName(File wfDirectory) {
		String result = null;
		WorkflowArtifacts artifacts = HadoopWorkflowUtils.getWorkflowArtifacts(wfDirectory);
		if (artifacts != null) {
			FileSystemApplicationContextFactory factory = new FileSystemApplicationContextFactory();
			factory.setApplicationContext(context);
			factory.setBeanClassLoader(artifacts.getWorkflowClassLoader());
			factory.setResource(artifacts.getWorkflowDescriptor());
			ConfigurableApplicationContext appContext = factory.createApplicationContext();
			Map<String, Job> springBatchJobs = appContext.getBeansOfType(Job.class);

			if (springBatchJobs.size() > 0) {
				for (String jobName : springBatchJobs.keySet()) {
					if (isJobRegistered(jobName)) {
						result = jobName;
						break;
					}
				}
			}
			else {
				String jobName = HadoopWorkflowUtils.generateSpringBatchJobName(artifacts);
				if (isJobRegistered(jobName)) {
					result = jobName;
				}
			}
		}
		else {
			result = null;
		}
		return result;
	}


	private boolean isJobRegistered(String jobName) {
		boolean result = true;
		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		try {
			Job job = jobRegistry.getJob(jobName);
			if (job == null) {
				result = false;
			}
		} catch (NoSuchJobException e) {
			result = false;
		}
		return result;
	}


}
