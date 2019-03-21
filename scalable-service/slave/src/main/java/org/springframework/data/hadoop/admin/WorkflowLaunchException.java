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
 * @author Jarred Li
 *
 */
public class WorkflowLaunchException extends SpringHadoopAdminException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8921974234676970091L;

	public WorkflowLaunchException(){
		super();
	}
	
	public WorkflowLaunchException(String message){
		super(message);
	}
	
	public WorkflowLaunchException(String message, Throwable t){
		super(message,t);
	}
}
