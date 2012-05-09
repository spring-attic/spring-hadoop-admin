/*
 * Copyright 2011-2012 the original author or authors.
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
package org.springframework.data.hadoop.admin.util;

import java.net.URL;
import java.net.URLClassLoader;

import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author Jarred Li
 *
 */
public class ClassLoaderTest {

	public static void main(String[] args) throws Exception{
		ClassLoader mainLoader = ClassLoaderTest.class.getClassLoader();
		URL url = new URL("jar:file:/home/hadoop/hadoop-1.0.0/hadoop-examples-1.0.0.jar!/");
		//URL url2 = new URL("jar:file:/home/hadoop/hadoop-1.0.0/hadoop-core-1.0.0.jar!/");		
		ClassLoader loader = new URLClassLoader(new URL[]{url},mainLoader);
		FileSystemXmlApplicationContext ctx = new FileSystemXmlApplicationContext(
				new String[]{"file:///data2/spring-hadoop-admin/context.xml"},false);
		ctx.setClassLoader(loader);
		System.out.println("ctx ready.");
		ctx.refresh();
		System.out.println("job running");
		ctx.registerShutdownHook();
		

	}
}
