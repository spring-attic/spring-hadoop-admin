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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.support.ApplicationContextFactory;
import org.springframework.batch.core.configuration.support.ClassPathXmlApplicationContextFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.CustomEditorConfigurer;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.data.hadoop.admin.util.HadoopWorkflowUtils;
import org.springframework.util.Assert;

/**
 * <code>ApplicationContextFactory</code> which is used to create <code>FileSystemApplicationContextFactory</code>
 * 
 * @author Jarred Li
 *
 */
public class FileSystemApplicationContextFactory implements ApplicationContextFactory, ApplicationContextAware {

	private ClassLoader beanClassLoader;

	private static final Log logger = LogFactory.getLog(FileSystemApplicationContextFactory.class);

	private Resource resource;

	private ConfigurableApplicationContext parent;

	private boolean copyConfiguration = true;

	private Collection<Class<? extends BeanFactoryPostProcessor>> beanFactoryPostProcessorClasses;

	private Collection<Class<?>> beanPostProcessorExcludeClasses;

	/**
	 * Convenient constructor for configuration purposes.
	 */
	public FileSystemApplicationContextFactory() {
		this(null);
	}

	/**
	 * Create a factory instance with the resource specified. The resource is a
	 * Spring XML configuration file.
	 */
	public FileSystemApplicationContextFactory(Resource resource) {

		this.resource = resource;
		beanFactoryPostProcessorClasses = new ArrayList<Class<? extends BeanFactoryPostProcessor>>();
		beanFactoryPostProcessorClasses.add(PropertyPlaceholderConfigurer.class);
		beanFactoryPostProcessorClasses.add(CustomEditorConfigurer.class);
		beanPostProcessorExcludeClasses = new ArrayList<Class<?>>();
		/*
		 * Assume that a BeanPostProcessor that is BeanFactoryAware must be
		 * specific to the parent and remove it from the child (e.g. an
		 * AutoProxyCreator will not work properly). Unfortunately there might
		 * still be a a BeanPostProcessor with a dependency that itself is
		 * BeanFactoryAware, but we can't legislate for that here.
		 */
		beanPostProcessorExcludeClasses.add(BeanFactoryAware.class);
	}

	/**
	 * Setter for the path to the xml to load to create an
	 * {@link ApplicationContext}. Use imports to centralise the configuration
	 * in one file.
	 * 
	 * @param resource the resource path to the xml to load for the child
	 * context.
	 */
	public void setResource(Resource resource) {
		this.resource = resource;
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
	 * Protected access for subclasses to the flag determining whether
	 * configuration should be copied from parent context.
	 * 
	 * @return the flag value
	 */
	protected final boolean isCopyConfiguration() {
		return copyConfiguration;
	}

	/**
	 * Determines which bean factory post processors (like property
	 * placeholders) should be copied from the parent context. Defaults to
	 * {@link PropertyPlaceholderConfigurer} and {@link CustomEditorConfigurer}.
	 * 
	 * @param copyBeanFactoryPostProcessors the flag value to set
	 */

	public void setBeanFactoryPostProcessorClasses(Class<? extends BeanFactoryPostProcessor>[] beanFactoryPostProcessorClasses) {
		this.beanFactoryPostProcessorClasses = new ArrayList<Class<? extends BeanFactoryPostProcessor>>();
		for (int i = 0; i < beanFactoryPostProcessorClasses.length; i++) {
			this.beanFactoryPostProcessorClasses.add(beanFactoryPostProcessorClasses[i]);
		}
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
		this.beanPostProcessorExcludeClasses = new ArrayList<Class<?>>();
		for (int i = 0; i < beanPostProcessorExcludeClasses.length; i++) {
			this.beanPostProcessorExcludeClasses.add(beanPostProcessorExcludeClasses[i]);
		}

	}

	/**
	 * Protected access to the list of bean factory post processor classes that
	 * should be copied over to the context from the parent.
	 * 
	 * @return the classes for post processors that were nominated for copying
	 */
	protected final Collection<Class<? extends BeanFactoryPostProcessor>> getBeanFactoryPostProcessorClasses() {
		return beanFactoryPostProcessorClasses;
	}

	/**
	 * Setter for the parent application context.
	 * 
	 * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
	 */
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		if (applicationContext == null) {
			return;
		}
		Assert.isInstanceOf(ConfigurableApplicationContext.class, applicationContext);
		parent = (ConfigurableApplicationContext) applicationContext;
	}

	/**
	 * Creates an {@link ApplicationContext} from the provided path.
	 * 
	 * @see ApplicationContextFactory#createApplicationContext()
	 */
	public ConfigurableApplicationContext createApplicationContext() {
		logger.info("resource is:" + resource);
		if (resource == null) {
			return parent;
		}
		try {
			logger.info("create application context, resource:" + resource.getFile().getAbsolutePath()
					+ ", classloader:" + this.beanClassLoader + ". Parent is:" + parent);
			FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext();
			context.setClassLoader(this.beanClassLoader);
			context.setParent(parent);
			context.setConfigLocation(HadoopWorkflowUtils.fileURLPrefix + resource.getFile().getAbsolutePath());
			context.refresh();
			return context;
		} catch (Exception e) {
			try {
				logger.error("create application context fail. with resource:" + resource.getFile().getAbsolutePath(),
						e);
			} catch (IOException e1) {
				logger.error("log error", e1);
			}
		}
		return parent;
	}

	/**
	 * Extension point for special subclasses that want to do more complex
	 * things with the context prior to refresh. The default implementation
	 * does nothing.
	 * 
	 * @param parent the parent for the new application context
	 * @param context the new application context before it is refreshed, but
	 * after bean factory is initialized
	 * 
	 * @see ClassPathXmlApplicationContextFactory#setBeanFactoryPostProcessorClasses(Class[])
	 */
	protected void prepareContext(ConfigurableApplicationContext parent, ConfigurableApplicationContext context) {
	}

	/**
	 * Extension point for special subclasses that want to do more complex
	 * things with the bean factory prior to refresh. The default implementation
	 * copies all configuration from the parent according to the
	 * {@link #setCopyConfiguration(boolean) flag} set.
	 * 
	 * @param parent the parent bean factory for the new context (will never be
	 * null)
	 * @param beanFactory the new bean factory before bean definitions are
	 * loaded
	 * 
	 * @see ClassPathXmlApplicationContextFactory#setCopyConfiguration(boolean)
	 * @see DefaultListableBeanFactory#copyConfigurationFrom(ConfigurableBeanFactory)
	 */
	protected void prepareBeanFactory(DefaultListableBeanFactory parent, DefaultListableBeanFactory beanFactory) {
		if (copyConfiguration && parent != null) {
			beanFactory.copyConfigurationFrom(parent);
			List<BeanPostProcessor> beanPostProcessors = beanFactory.getBeanPostProcessors();
			for (BeanPostProcessor beanPostProcessor : new ArrayList<BeanPostProcessor>(beanPostProcessors)) {
				for (Class<?> cls : beanPostProcessorExcludeClasses) {
					if (cls.isAssignableFrom(beanPostProcessor.getClass())) {
						logger.debug("Removing bean post processor: " + beanPostProcessor + " of type " + cls);
						beanPostProcessors.remove(beanPostProcessor);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return "FileSystemApplicationContextFactory [resource=" + resource + "]";
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		return toString().equals(obj.toString());
	}

	/**
	 * @return the beanClassLoader
	 */
	public ClassLoader getBeanClassLoader() {
		return beanClassLoader;
	}

	/**
	 * @param beanClassLoder the beanClassLoder to set
	 */
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

}
