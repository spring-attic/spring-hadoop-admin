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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;


/**
 * To create <code>FileSystemApplicationContextFactory</code> from specified <code>WorkflowArtifacts</code>
 * 
 * @author Jarred Li
 *
 */
public class FileSystemApplicationContextsFactoryBean implements FactoryBean<Object>, ApplicationContextAware {

	private static final Log logger = LogFactory.getLog(FileSystemApplicationContextsFactoryBean.class);

	private boolean copyConfiguration = true;

	private Class<? extends BeanFactoryPostProcessor>[] beanFactoryPostProcessorClasses;

	private Class<?>[] beanPostProcessorExcludeClasses;

	private ApplicationContext applicationContext;

	private List<WorkflowArtifacts> workflowArtifacts = new ArrayList<WorkflowArtifacts>();


	public void setWorkflowArtifacts(WorkflowArtifacts[] workflowArtifacts) {
		if (workflowArtifacts == null || workflowArtifacts.length == 0) {
			return;
		}
		this.workflowArtifacts = Arrays.asList(workflowArtifacts);
	}

	/**
	 * Flag to indicate that configuration such as bean post processors and
	 * custom editors should be copied from the parent context. Defaults to
	 * true.
	 * 
	 * @param copyConfiguration the flag value to set
	 */
	public void setCopyConfiguration(boolean copyConfiguration) {
		this.copyConfiguration = copyConfiguration;
	}

	/**
	 * Determines which bean factory post processors (like property
	 * placeholders) should be copied from the parent context. Defaults to
	 * {@link PropertyPlaceholderConfigurer} and {@link CustomEditorConfigurer}.
	 * 
	 * @param copyBeanFactoryPostProcessors the flag value to set
	 */

	public void setBeanFactoryPostProcessorClasses(Class<? extends BeanFactoryPostProcessor>[] beanFactoryPostProcessorClasses) {
		this.beanFactoryPostProcessorClasses = beanFactoryPostProcessorClasses;
	}

	/**
	 * Determines by exclusion which bean post processors should be copied from
	 * the parent context. Defaults to {@link BeanFactoryAware} (so any post
	 * processors that have a reference to the parent bean factory are not
	 * copied into the child). Note that these classes do not themselves have to
	 * be {@link BeanPostProcessor} implementations or sub-interfaces.
	 * 
	 * @param beanPostProcessorExcludeClasses the classes to set
	 */
	public void setBeanPostProcessorExcludeClasses(Class<?>[] beanPostProcessorExcludeClasses) {
		this.beanPostProcessorExcludeClasses = beanPostProcessorExcludeClasses;
	}

	/**
	 * Create an {@link ApplicationContextFactory} from each resource provided
	 * in {@link #setResources(Resource[])}.
	 * 
	 * @return an array of {@link ApplicationContextFactory}
	 * @throws Exception
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public Object getObject() throws Exception {
		if (workflowArtifacts == null) {
			return new ApplicationContextFactory[0];
		}

		List<ApplicationContextFactory> applicationContextFactories = new ArrayList<ApplicationContextFactory>();
		for (WorkflowArtifacts artifacts : workflowArtifacts) {
			logger.info("workflow artifacts:" + artifacts.getWorkflowDescriptor().getFile().getAbsolutePath()
					+ ",classloader:" + artifacts.getWorkflowClassLoader());
			if (HadoopWorkflowUtils.isSpringBatchJob(applicationContext, artifacts)) {
				logger.info("is spring batch job, will be loaded automatically");
				FileSystemApplicationContextFactory factory = new FileSystemApplicationContextFactory();
				factory.setCopyConfiguration(copyConfiguration);
				if (beanFactoryPostProcessorClasses != null) {
					factory.setBeanFactoryPostProcessorClasses(beanFactoryPostProcessorClasses);
				}
				if (beanPostProcessorExcludeClasses != null) {
					factory.setBeanPostProcessorExcludeClasses(beanPostProcessorExcludeClasses);
				}
				factory.setBeanClassLoader(artifacts.getWorkflowClassLoader());
				factory.setResource(artifacts.getWorkflowDescriptor());
				factory.setApplicationContext(applicationContext);
				applicationContextFactories.add(factory);
			}
			else {
				logger.info("not spring batch job, will create new spring batch job");
				HadoopWorkflowUtils.createAndRegisterSpringBatchJob(applicationContext, artifacts);
			}
		}
		return applicationContextFactories.toArray(new ApplicationContextFactory[applicationContextFactories.size()]);
	}

	/**
	 * The type of object returned by this factory - an array of
	 * {@link ApplicationContextFactory}.
	 * 
	 * @return array of {@link ApplicationContextFactory}
	 * @see FactoryBean#getObjectType()
	 */
	public Class<?> getObjectType() {
		return ApplicationContextFactory[].class;
	}

	/**
	 * Optimization hint for bean factory.
	 * @return true
	 * @see FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * An application context that can be used as a parent context for all the
	 * factories.
	 * 
	 * @param applicationContext the {@link ApplicationContext} to set
	 * @throws BeansException
	 * @see ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

}
