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
package org.springframework.data.hadoop.admin.cli.commands;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.admin.cli.util.Log;
import org.springframework.data.hadoop.admin.cli.util.PropertyUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * @author Jarred Li
 *
 */
public class BaseCommand {

	private String commandURL = "jobs.json";

	/**
	 * call rest service with "Get" 
	 */
	public void callGetService() {
		RestTemplate template = getRestTemplate();
		String json = template.getForObject(getCommandUrl(), String.class);
		Log.show(json);
	}


	/**
	 * call rest service with "Post" 
	 * @param <T>
	 */
	public <T> void callPostService(T object) {
		RestTemplate template = getRestTemplate();
//		String message = template.postForObject(getCommandUrl(), object, String.class);
		HttpEntity<T> entity = new HttpEntity<T>(object);
		ResponseEntity<String> response = template.exchange(getCommandUrl(), HttpMethod.POST, entity, String.class);
		String message = response.getBody();
		if(message != null){
			Log.show(message);
		}
	}

	/**
	 * call rest service with "Delete" 
	 */
	public void callDeleteService() {
		RestTemplate template = getRestTemplate();
		template.delete(getCommandUrl());
	}

	/**
	 * get RestTempate from xml Beans.
	 * 
	 * @return
	 */
	private RestTemplate getRestTemplate() {
		ApplicationContext context = new ClassPathXmlApplicationContext("rest-context.xml");
		RestTemplate template = context.getBean("restTemplate", RestTemplate.class);
		return template;
	}

	/**
	 * get command URL
	 * 
	 * @return
	 */
	private String getCommandUrl() {
		try {
			String serviceUrl = PropertyUtil.getTargetURl();
			if(serviceUrl == null || serviceUrl.length() == 0){
				Log.error("you must set Spring Hadoop Admin service URL first by running target command");
			}
			if (!serviceUrl.endsWith("/")) {
				serviceUrl += "/";
			}
			serviceUrl += getCommandURL();
			return serviceUrl;
		} catch (Exception e) {
			Log.error("get service url failed. " + e.getMessage());
		}
		return null;
	}

	/**
	 * @return the commandURL
	 */
	public String getCommandURL() {
		return commandURL;
	}

	/**
	 * @param commandURL the commandURL to set
	 */
	public void setCommandURL(String commandURL) {
		this.commandURL = commandURL;
	}


}
