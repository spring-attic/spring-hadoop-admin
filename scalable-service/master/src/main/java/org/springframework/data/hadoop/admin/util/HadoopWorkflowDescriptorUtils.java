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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.xml.DefaultDocumentLoader;
import org.springframework.beans.factory.xml.DelegatingEntityResolver;
import org.springframework.beans.factory.xml.DocumentLoader;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.hadoop.admin.SpringHadoopAdminWorkflowException;
import org.springframework.util.xml.SimpleSaxErrorHandler;
import org.springframework.util.xml.XmlValidationModeDetector;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;

/**
 * Utility class for hadoop workflow descriptor
 * 
 * @author Jarred Li
 *
 */
public class HadoopWorkflowDescriptorUtils {

	private static final Log logger = LogFactory.getLog(HadoopWorkflowDescriptorUtils.class);

	private ClassLoader beanClassLoader = Thread.currentThread().getContextClassLoader();

	private ResourceLoader resourceLoader = new PathMatchingResourcePatternResolver();

	private static String springContextNameSpace = "http://www.springframework.org/schema/context";

	private static String placePlaceHolderTagName = "property-placeholder";

	private DocumentLoader documentLoader = new DefaultDocumentLoader();

	private EntityResolver entityResolver;

	private ErrorHandler errorHandler = new SimpleSaxErrorHandler(logger);

	private String replacePropertyPlaceHolderFaiure = "replace property place holder failed.";

	/**
	 * replace the "property-placeholder" element in beans XML with absolute path 
	 * where the uploaded file exist in the server.
	 *  
	 * @param workflowDescriptor spring's XML file
	 * @param workflowProperty properties used in spring's XML file
	 * 
	 * @return whether "property-placeholder" appears in the beans XML. 
	 * 		   true - it appears
	 * 		   false - no
	 * 
	 * @throws SpringHadoopAdminWorkflowException
	 */
	public boolean replacePropertyPlaceHolder(File workflowDescriptor, File workflowProperty)
			throws SpringHadoopAdminWorkflowException {
		boolean result = false;
		String parentFolder = workflowDescriptor.getParentFile().getAbsolutePath();
		parentFolder = parentFolder + File.separator;
		try {
			BufferedInputStream bis = new BufferedInputStream(new FileInputStream(workflowDescriptor));
			InputSource inputSource = new InputSource(bis);
			Document doc = this.documentLoader.loadDocument(inputSource, getEntityResolver(), this.errorHandler,
					XmlValidationModeDetector.VALIDATION_NONE, true);
			NodeList nodes = doc.getElementsByTagNameNS(springContextNameSpace, placePlaceHolderTagName);
			logger.debug("number of nodes:" + nodes.getLength());
			for (int i = 0, j = nodes.getLength(); i < j; i++) {
				Node node = nodes.item(i);
				if (node instanceof Element) {
					Element e = (Element) node;
					String newValue = workflowProperty.getAbsolutePath().replace("\\", "/");
					newValue = HadoopWorkflowUtils.fileURLPrefix + newValue;
					e.setAttribute("location", newValue);
					result = true;
					logger.debug("new location after replaced:" + newValue);
				}
			}
			if (result) {
				TransformerFactory transformerFactory = TransformerFactory.newInstance();
				Transformer transformer = transformerFactory.newTransformer();
				DOMSource source = new DOMSource(doc);
				StreamResult out = new StreamResult(new File(workflowDescriptor.getAbsolutePath()));
				transformer.transform(source, out);
			}
		} catch (Exception e) {
			logger.warn(replacePropertyPlaceHolderFaiure);
			throw new SpringHadoopAdminWorkflowException(replacePropertyPlaceHolderFaiure, e);
		}
		return result;
	}

	/**
	 * replace jar file name with full path where the uploaded jar in the server.
	 * 
	 * @param workflowProperty workflow property file
	 * 
	 * @throws ConfigurationException 
	 */
	public void replaceJarPath(File workflowProperty) throws ConfigurationException {
		PropertiesConfiguration config = new PropertiesConfiguration(workflowProperty);
		String workflowFolder = workflowProperty.getParent();
		workflowFolder.replace("\\", "/");
		workflowFolder = HadoopWorkflowUtils.fileURLPrefix + workflowFolder;
		config.setProperty("jar.path", workflowFolder);
		config.save();
	}

	/**
	 * create new entity resolver <code>org.xml.sax.EntityResolver</code>
	 * 
	 * @return 
	 */
	protected EntityResolver getEntityResolver() {
		if (this.entityResolver == null) {
			// Determine default EntityResolver to use.
			ResourceLoader resourceLoader = getResourceLoader();
			if (resourceLoader != null) {
				this.entityResolver = new ResourceEntityResolver(resourceLoader);
			}
			else {
				this.entityResolver = new DelegatingEntityResolver(getBeanClassLoader());
			}
		}
		return this.entityResolver;
	}

	/**
	 * get ClassLoader which is used to load Spring Beans
	 * 
	 * @return ClassLoader to load Beans
	 */
	public ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}

	/**
	 * set ClassLoader which is used to load Spring Beans
	 * 
	 * @param beanClassLoader 
	 */
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

	/**
	 * set ResourceLoader which is used to load resources
	 * 
	 * @param resourceLoader
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * get ResouseLoader which is used to load resource
	 * 
	 * @return ResouseLoader which is used to load resource
	 */
	public ResourceLoader getResourceLoader() {
		return this.resourceLoader;
	}

}
