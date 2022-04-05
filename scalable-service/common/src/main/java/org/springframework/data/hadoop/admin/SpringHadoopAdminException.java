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
package org.springframework.data.hadoop.admin;

/**
 * Base class for Spring Hadoop Admin exception handling
 * 
 * @author Jarred Li
 *
 */
public class SpringHadoopAdminException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2954725715945849099L;


	/**
	 * Default constructor
	 * 
	 */
	public SpringHadoopAdminException() {
		super();
	}

	/**
	 * constructor with message
	 * 
	 * @param message The message for this exception
	 */
	public SpringHadoopAdminException(String message) {
		super(message);
	}

	/**
	 * constructor with message and throwable
	 * 
	 * @param message The message for this exception
	 * @param t 
	 */
	public SpringHadoopAdminException(String message, Throwable t) {
		super(message, t);
	}
}
