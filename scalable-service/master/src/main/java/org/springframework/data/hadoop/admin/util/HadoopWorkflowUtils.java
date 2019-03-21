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
package org.springframework.data.hadoop.admin.util;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.configuration.JobFactory;
import org.springframework.batch.core.configuration.JobRegistry;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.AutomaticJobRegistrar;
import org.springframework.batch.core.configuration.support.DefaultJobLoader;
import org.springframework.batch.core.configuration.support.ReferenceJobFactory;
import org.springframework.batch.core.job.SimpleJob;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.admin.SpringHadoopAdminWorkflowException;
import org.springframework.data.hadoop.admin.service.FileInfo;
import org.springframework.data.hadoop.admin.workflow.HadoopWorkflowLaunchRequestAdapter;
import org.springframework.data.hadoop.admin.workflow.SimpleSpringHadoopTasklet;
import org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextFactory;
import org.springframework.data.hadoop.admin.workflow.support.FileSystemApplicationContextsFactoryBean;
import org.springframework.data.hadoop.admin.workflow.support.WorkflowArtifacts;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;

/**
 * Utility class for Spring Hadoop workflow
 * 
 * @author Jarred Li
 *
 */
public class HadoopWorkflowUtils {

	public static String fileURLPrefix = System.getProperty("os.name").toLowerCase().contains("windows") ? "file:///" : "file://";

	private static final Log logger = LogFactory.getLog(HadoopWorkflowUtils.class);

	public static String springHadoopJobPrefix = "spring-hadoop-job-";

	public static String springHadoopTaskName = "spring-hadoop-step";


	/**
	 * get workflow artifacts aka, workflow descriptor and classloader from the specified folder 
	 * 
	 * @param workflowArtifactFolder folder contains artifacts
	 * @return  <code>WorkflowArtifacts</code>
	 */
	public static WorkflowArtifacts getWorkflowArtifacts(File workflowArtifactFolder) {
		if (!workflowArtifactFolder.exists()) {
			return null;
		}

		WorkflowArtifacts result = null;
		String[] descriptor = getWorkflowDescriptor(workflowArtifactFolder);
		if (descriptor == null) {
			return null;
		}
		String workflowDescriptorFileName = descriptor[0];
		Resource resource = new FileSystemResource(new File(workflowDescriptorFileName));

		URL[] urls = HadoopWorkflowUtils.getWorkflowLibararies(workflowArtifactFolder);
		if (urls == null || urls.length == 0) {
			return null;
		}
		ClassLoader parentLoader = HadoopWorkflowUtils.class.getClassLoader();
		ClassLoader loader = new URLClassLoader(urls, parentLoader);

		result = new WorkflowArtifacts(resource, loader);
		return result;
	}

	/**
	 * get the workflow descriptor file in XML.  
	 * 
	 * @param workflowArtifactFolder job artifacts folder.
	 *  
	 * @return Spring Hadoop job descriptor file and property file.
	 */
	public static String[] getWorkflowDescriptor(File workflowArtifactFolder) {
		String result[] = null;
		if (!workflowArtifactFolder.exists()) {
			return result;
		}

		File[] xmlFiles = workflowArtifactFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".xml");
			}

		});

		File[] propertiesFiles = workflowArtifactFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".properties");
			}

		});

		if (xmlFiles != null && xmlFiles.length == 1 && propertiesFiles != null && propertiesFiles.length == 1) {
			result = new String[2];
			result[0] = xmlFiles[0].getAbsolutePath();
			result[1] = propertiesFiles[0].getAbsolutePath();
		}
		else {
			logger.warn("uploaded workflow descriptor is incorrect");
		}
		return result;
	}

	public static boolean isValidWorkflow(List<FileInfo> files){
		if(files == null || files.size() < 3){
			return false;
		}
		boolean hasJar = false;
		boolean hasProperties = false;
		boolean hasXml = false;
		for(FileInfo fi : files){
			if(fi.getFileName().toLowerCase().endsWith(".jar")){
				hasJar = true;
				continue;
			}
			if(fi.getFileName().toLowerCase().endsWith(".properties")){
				hasProperties = true;
				continue;
			}
			if(fi.getFileName().toLowerCase().endsWith(".xml")){
				hasXml = true;
				continue;
			}
			if(hasJar && hasProperties && hasXml){
				break;
			}
		}
		return hasJar && hasProperties && hasXml;
	}

	
	/**
	 * get jars as URL list.
	 * 
	 * @param workflowArtifactFolder parent folder 
	 * 
	 * @return array of URL
	 */
	public static URL[] getWorkflowLibararies(File workflowArtifactFolder) {
		if (!workflowArtifactFolder.exists()) {
			return null;
		}
		List<URL> result = new ArrayList<URL>();

		File[] jarFiles = workflowArtifactFolder.listFiles(new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				return name.toLowerCase().endsWith(".jar");
			}

		});
		if (jarFiles != null && jarFiles.length > 0) {
			for (File jarFile : jarFiles) {
				try {
					result.add(new URL("jar:file:" + jarFile.getAbsolutePath() + "!/"));
				} catch (MalformedURLException e) {
					logger.warn("invalid uploaded jar");
				}
			}
		}
		logger.info("HadoopWorkflowUtils::getWorkflowLibararies, number of jar:" + result.size());
		return result.toArray(new URL[0]);
	}

	/**
	 * get workflow class loader with all uploaded jar
	 * 
	 * @param workflowArtifactFolder
	 * @return workflow class loader
	 */
	public static ClassLoader getWorkflowClassLoader(File workflowArtifactFolder) {
		ClassLoader parentLoader = HadoopWorkflowUtils.class.getClassLoader();
		if (!workflowArtifactFolder.exists()) {
			return parentLoader;
		}
		URL[] urls = getWorkflowLibararies(workflowArtifactFolder);
		if (urls != null && urls.length > 0) {
			ClassLoader loader = new URLClassLoader(urls, parentLoader);
			return loader;
		}
		else {
			return parentLoader;
		}

	}

	/**
	 * To judge Whether the uploaded workflow descriptor is Spring Batch Job
	 * 
	 * @param context Root ApplicationContext
	 * @param artifacts workflow atifacts
	 * @return true: if the workflow descritpor is Spring Batch Job
	 * 		   false: otherwise
	 * @throws SpringHadoopAdminWorkflowException
	 */
	public static boolean isSpringBatchJob(ApplicationContext context, WorkflowArtifacts artifacts)
			throws SpringHadoopAdminWorkflowException {
		if (context == null) {
			logger.warn("root applicaton context is null");
			throw new SpringHadoopAdminWorkflowException("root applicaton context is null");
		}
		if (artifacts == null) {
			logger.warn("workflow artifacts is null");
			throw new SpringHadoopAdminWorkflowException("workflow artifacts is null");
		}

		boolean result = true;
		FileSystemApplicationContextFactory factory = new FileSystemApplicationContextFactory();
		factory.setApplicationContext(context);
		factory.setBeanClassLoader(artifacts.getWorkflowClassLoader());
		factory.setResource(artifacts.getWorkflowDescriptor());
		ConfigurableApplicationContext appContext = factory.createApplicationContext();
		Map<String, Job> springBatchJobs = appContext.getBeansOfType(Job.class);
		result = springBatchJobs.size() > 0;
		return result;
	}

	/**
	 * create and register Spring Batch job
	 * 
	 * @param context root application context
	 * @param artifacts workflow artifacts which include workflow descriptor and class loader information.
	 * @throws Exception
	 */
	public static void createAndRegisterSpringBatchJob(ApplicationContext context, final WorkflowArtifacts artifacts)
			throws SpringHadoopAdminWorkflowException {
		if (context == null) {
			logger.warn("root applicaton context is null");
			throw new SpringHadoopAdminWorkflowException("root applicaton context is null");
		}
		String workflowDescriptor = getWorkflowDescriptor(artifacts);
		logger.info("create spring batch job:" + workflowDescriptor + ", classloader:"
				+ artifacts.getWorkflowClassLoader());

		final FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext();
		ctx.setClassLoader(artifacts.getWorkflowClassLoader());
		ctx.setConfigLocation(HadoopWorkflowUtils.fileURLPrefix + workflowDescriptor);

		try {
			SimpleJob job = new SimpleJob(generateSpringBatchJobName(artifacts));
			TaskletStep step = new TaskletStep(springHadoopTaskName);
			SimpleSpringHadoopTasklet tasklet = new SimpleSpringHadoopTasklet();
			tasklet.setContext(ctx);
			step.setTasklet(tasklet);
			JobRepository jobRepository = context.getBean(JobRepository.class);
			DataSourceTransactionManager transactionManager = context.getBean("transactionManager",
					DataSourceTransactionManager.class);
			step.setTransactionManager(transactionManager);
			step.setJobRepository(jobRepository);
			step.afterPropertiesSet();
			job.addStep(step);
			JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
			job.setJobRepository(jobRepository);
			job.afterPropertiesSet();
			JobFactory jobFactory = new ReferenceJobFactory(job);
			jobRegistry.register(jobFactory);
		} catch (Exception e) {
			logger.warn("create and register Spring Batch Job failed");
			throw new SpringHadoopAdminWorkflowException("create and register Spring Batch Job failed", e);
		}
	}

	/**
	 * generate spring batch job name based on workflow descriptor name
	 * 
	 * @param artifacts workflow artifact
	 * 
	 * @return new generated job name
	 */
	public static String generateSpringBatchJobName(final WorkflowArtifacts artifacts) {
		return springHadoopJobPrefix + artifacts.getWorkflowDescriptor().getFilename();
	}

	/**
	 * unregister spring batch job.
	 * 
	 * @param context root application context
	 * @param springBatchJobName job name to be unregistered
	 */
	public static void unregisterSpringBatchJob(ApplicationContext context, String springBatchJobName) {
		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		jobRegistry.unregister(springBatchJobName);
		logger.info("unregister spring batch job:" + springBatchJobName);
	}

	/**
	 * get workflow descriptor file name
	 * 
	 * @param artifacts workflow artificats	 * 
	 * @return The workflow descriptor file name
	 * @throws SpringHadoopAdminWorkflowException
	 */
	public static String getWorkflowDescriptor(final WorkflowArtifacts artifacts)
			throws SpringHadoopAdminWorkflowException {
		if (artifacts == null) {
			logger.warn("workflow artifacts is null");
			throw new SpringHadoopAdminWorkflowException("workflow artifacts is null");
		}

		String workflowDescriptor = null;

		try {
			Resource resource = artifacts.getWorkflowDescriptor();
			if (resource == null) {
				throw new SpringHadoopAdminWorkflowException("workflow's descriptor is null");
			}
			File file = resource.getFile();
			if (file == null) {
				throw new SpringHadoopAdminWorkflowException("workflow's descriptor is null");
			}
			workflowDescriptor = file.getAbsolutePath();
		} catch (IOException e) {
			throw new SpringHadoopAdminWorkflowException("IO error when trying to get workflow's descriptor.");
		}
		return workflowDescriptor;
	}

	

	/**
	 * 
	 * @param parentFolder
	 * @param context
	 */
	public static void processAndRegisterWorkflow(File parentFolder, ApplicationContext context) {
		try {
			WorkflowArtifacts artifacts = processUploadedFile(parentFolder);
			if (artifacts == null) {
				return;
			}

			boolean isSpringBatchJob = HadoopWorkflowUtils.isSpringBatchJob(context, artifacts);
			logger.info("uploaded workflow artifacts is spring batch job? " + isSpringBatchJob);
			if (isSpringBatchJob) {
				registerSpringBatchJob(artifacts, context);
			}
			else {
				HadoopWorkflowUtils.createAndRegisterSpringBatchJob(context, artifacts);
			}
		} catch (Exception e) {
			logger.error("handle uploaded file failed", e);
		}
	}

	/**
	 * process the uploaded file. Replace context place holder locaton, update jar path.
	 * 
	 * @param parentFolder parent folder of workflow files that are being uploaded
	 * @return <code>WorkflowArtifacts<code> when all jar, descriptor, properties are uploaded
	 * 			otherwise, null 
	 * @throws ConfigurationException 
	 * @throws SpringHadoopAdminWorkflowException 
	 */
	public static WorkflowArtifacts processUploadedFile(final File parentFolder) throws ConfigurationException,
			SpringHadoopAdminWorkflowException {		
		String[] descriptor = HadoopWorkflowUtils.getWorkflowDescriptor(parentFolder);
		URL[] urls = HadoopWorkflowUtils.getWorkflowLibararies(parentFolder);
		if (descriptor == null || urls == null || urls.length == 0) {
			return null;
		}

		ClassLoader parentLoader = HadoopWorkflowLaunchRequestAdapter.class.getClassLoader();
		ClassLoader loader = new URLClassLoader(urls, parentLoader);

		String workflowDescriptorFileName = descriptor[0];
		String workflowPropertyFileName = descriptor[1];
		File workflowPropertyFile = new File(workflowPropertyFileName);
		File workflowDescriptorFile = new File(workflowDescriptorFileName);
		HadoopWorkflowDescriptorUtils util = new HadoopWorkflowDescriptorUtils();
		util.setBeanClassLoader(loader);

		util.replaceJarPath(workflowPropertyFile);
		boolean replaced = util.replacePropertyPlaceHolder(workflowDescriptorFile, workflowPropertyFile);

		if (!replaced) {
			throw new SpringHadoopAdminWorkflowException(
					"there is no property place holder in the workflow descriptor. MapReduce jar may not be found");
		}

		Resource resource = new FileSystemResource(new File(workflowDescriptorFileName));
		WorkflowArtifacts artifacts = new WorkflowArtifacts(resource, loader);
		return artifacts;
	}

	/**
	 * register the new uploaded Spring Batch job 
	 * 
	 * @param artifacts workflow artifacts
	 * @param context root Application Context 
	 * 
	 * @throws Exception
	 */
	public static void registerSpringBatchJob(WorkflowArtifacts artifacts, ApplicationContext context) throws Exception {
		String workflowDescriptor = HadoopWorkflowUtils.getWorkflowDescriptor(artifacts);
		logger.info("create spring batch job:" + workflowDescriptor + ", classloader:"
				+ artifacts.getWorkflowClassLoader());
		AutomaticJobRegistrar registarar = new AutomaticJobRegistrar();
		FileSystemApplicationContextsFactoryBean factoryBean = new FileSystemApplicationContextsFactoryBean();
		factoryBean.setApplicationContext(context);
		factoryBean.setWorkflowArtifacts(new WorkflowArtifacts[] { artifacts });
		registarar.setApplicationContextFactories((ApplicationContextFactory[]) factoryBean.getObject());
		DefaultJobLoader jobLoader = new DefaultJobLoader();
		JobRegistry jobRegistry = context.getBean("jobRegistry", JobRegistry.class);
		jobLoader.setJobRegistry(jobRegistry);
		registarar.setJobLoader(jobLoader);
		registarar.start();
		logger.info("successfully register spring batch job");
	}
}
