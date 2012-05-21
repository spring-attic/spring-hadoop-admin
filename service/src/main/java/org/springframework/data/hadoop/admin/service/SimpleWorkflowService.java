/*
 * Copyright 2009-2010 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.admin.service.FileInfo;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.launch.NoSuchJobException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.data.hadoop.admin.util.Constant;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;
import org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextFactory;
import org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts;

import org.springframework.util.Assert;

/**
 * 
 * @author Dave Syer
 * @author Jarred Li
 * 
 */
public class SimpleWorkflowService implements WorkflowService, InitializingBean, ResourceLoaderAware,
		ApplicationContextAware {

	private File outputDir = new File(System.getProperty("java.io.tmpdir", "/tmp"), Constant.WORKFLOW_LOCATION);

	private static final Log logger = LogFactory.getLog(SimpleWorkflowService.class);

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	private WorkflowSender workflowSender;

	private WorkflowRemover workflowRemover;

	private ApplicationContext context;


	public void setWorkflowRemover(WorkflowRemover workflowRemover) {
		this.workflowRemover = workflowRemover;
	}

	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public void setWorkflowSender(WorkflowSender workflowSender) {
		this.workflowSender = workflowSender;
	}

	/**
	 * The output directory to store new files. Defaults to
	 * <code>${java.io.tmpdir}/batch/files</code>.
	 * @param outputDir the output directory to set
	 */
	public void setOutputDir(File outputDir) {
		this.outputDir = outputDir;
	}

	public void afterPropertiesSet() throws Exception {
		Assert.state(workflowSender != null, "A WorkflowSender must be provided");
		Assert.state(workflowRemover != null, "A WorkflowRemover must be provided");
		if (!outputDir.exists()) {
			Assert.state(outputDir.mkdirs(), "Cannot create output directory " + outputDir);
		}
		Assert.state(outputDir.exists(), "Output directory does not exist " + outputDir);
		Assert.state(outputDir.isDirectory(), "Output file is not a directory " + outputDir);
	}

	public FileInfo createFile(String path) throws IOException {

		path = sanitize(path);

		Assert.hasText(path, "The file path must not be empty");

		String name = path.substring(path.lastIndexOf("/") + 1);
		String parent = path.substring(0, path.lastIndexOf(name));
		if (parent.endsWith("/")) {
			parent = parent.substring(0, parent.length() - 1);
		}

		File directory = new File(outputDir, parent);
		directory.mkdirs();
		Assert.state(directory.exists() && directory.isDirectory(), "Could not create directory: " + directory);

		FileInfo result = new FileInfo(path);
		File dest = new File(outputDir, result.getFileName());
		dest.createNewFile();

		return result;

	}

	/**
	 * @param target the target file
	 * @return the path to the file from the base output directory
	 */
	private String extractPath(File target) {
		String outputPath = outputDir.getAbsolutePath();
		return target.getAbsolutePath().substring(outputPath.length() + 1).replace("\\", "/");
	}

	public boolean publish(FileInfo dest) throws IOException {
		String path = dest.getPath();
		workflowSender.send(getResource(path).getFile());
		return true;
	}

	public int countFiles() {
		ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		Resource[] resources;
		try {
			resources = resolver.getResources("file:///" + outputDir.getAbsolutePath() + "/**");
		} catch (IOException e) {
			throw new IllegalStateException("Unexpected problem resolving files", e);
		}
		return resources.length;
	}

	public List<FileInfo> getFiles(int startFile, int pageSize) throws IOException {

		List<FileInfo> files = getFiles("**");

		String path = "";
		int count = 0;
		for (FileInfo info : files) {
			FileInfo shortInfo = info.shortPath();
			if (!path.equals(shortInfo.getPath())) {
				files.set(count, shortInfo);
				path = shortInfo.getPath();
			}
			count++;
		}

		return new ArrayList<FileInfo>(files.subList(startFile, Math.min(startFile + pageSize, files.size())));

	}

	private List<FileInfo> getFiles(String pattern) {
		ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		List<Resource> resources = new ArrayList<Resource>();

		if (!pattern.startsWith("/")) {
			pattern = "/" + outputDir.getAbsolutePath() + "/" + pattern;
		}
		if (!pattern.startsWith("file:")) {
			pattern = "file:///" + pattern;
		}

		try {
			resources = Arrays.asList(resolver.getResources(pattern));
		} catch (IOException e) {
			logger.debug("Cannot locate files " + pattern, e);
			return new ArrayList<FileInfo>();
		}

		List<FileInfo> files = new ArrayList<FileInfo>();
		for (Resource resource : resources) {
			File file;
			try {
				file = resource.getFile();
				if (file.isFile()) {
					FileInfo info = new FileInfo(extractPath(file));
					files.add(info);
				}
			} catch (IOException e) {
				logger.debug("Cannot locate file " + resource, e);
			}
		}

		Collections.sort(files);
		return new ArrayList<FileInfo>(files);

	}


	public List<WorkflowInfo> getWorkflows(int start, int pageSize) throws IOException {
		List<WorkflowInfo> wfInfoes = new ArrayList<WorkflowInfo>();
		if (outputDir.exists()) {
			logger.info("getWorkflows::workflow descriptor base directory:" + outputDir.getAbsolutePath());
			File[] wfArtifactsDirs = outputDir.listFiles();
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


	public int delete(String pattern) throws IOException {

		ResourcePatternResolver resolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		if (!pattern.startsWith("/")) {
			pattern = "/" + outputDir.getAbsolutePath() + "/" + pattern;
		}
		if (!pattern.startsWith("file:")) {
			pattern = "file:///" + pattern;
		}

		Resource[] resources = resolver.getResources(pattern);

		int count = 0;
		for (Resource resource : resources) {
			File file = resource.getFile();
			workflowRemover.beforeRemove(file);
			if (file.isFile()) {
				count++;
				logger.info("removing file:" + file.getAbsolutePath());
				FileUtils.deleteQuietly(file);
			}
		}

		return count;

	}

	public Resource getResource(String path) {

		path = sanitize(path);
		FileInfo pattern = new FileInfo(path);
		List<FileInfo> files = getFiles(pattern.getPattern());
		FileInfo info = files.isEmpty() ? pattern : files.get(0);
		File file = new File(outputDir, info.getFileName());
		return new FileServiceResource(file, path);

	}

	public File getUploadDirectory() {
		return outputDir;
	}

	/**
	 * Normalize file separators to "/" and strip leading prefix and separators
	 * to create a simple relative path.
	 * 
	 * @param path the raw path
	 * @return a sanitized version
	 */
	private String sanitize(String path) {
		path = path.replace("\\", "/");
		if (path.startsWith("files:")) {
			path = path.substring("files:".length());
			while (path.startsWith("/")) {
				path = path.substring(1);
			}
		}
		return path;
	}

	private static class FileServiceResource extends FileSystemResource implements ContextResource {

		private final String path;

		public FileServiceResource(File file, String path) {
			super(file);
			this.path = path;
		}

		public String getPathWithinContext() {
			return path;
		}

	}

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
		File folder = new File("/" + outputDir.getAbsolutePath() + "/" + path);
		HadoopWorkflowUtils.processAndRegisterWorkflow(folder, context);
	}


}
