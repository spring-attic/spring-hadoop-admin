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

import java.io.File;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.hadoop.admin.cli.util.Log;
import org.springframework.data.hadoop.admin.cli.util.PropertyUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.client.RestTemplate;

/**
 * Base class for REST call commands.
 * 
 * @author Jarred Li
 *
 */
public class BaseCommand {

	private String commandURL = "jobs.json";

	public static String errorConnection = "I/O error: Connection refused";

	public static String messageConnection = "Make sure you have set correct service URL, type \"info\" to check service URL."
			+ " Or check if the service is working";

	/**
	 * call rest service with "Get" 
	 */
	public void callGetService() {
		try {
			RestTemplate template = getRestTemplate();
			String json = template.getForObject(getCommandUrl(), String.class);
			Log.show(json);
		} catch (Throwable t) {
			showErrorMsg(t);
		}
	}


	/**
	 * @param t
	 */
	private void showErrorMsg(Throwable t) {
		if (t.getMessage().contains(errorConnection)) {
			Log.error("call service failed." + messageConnection);
		}
		else {
			Log.error("call service failed. Reason:" + t.getMessage());
		}
	}


	/**
	 * call rest service with "Post" 
	 * @param <T>
	 */
	public <T> void callPostService(T object) {
		try {
			RestTemplate template = getRestTemplate();
			//		String message = template.postForObject(getCommandUrl(), object, String.class);
			HttpEntity<T> entity = new HttpEntity<T>(object);
			ResponseEntity<String> response = template.exchange(getCommandUrl(), HttpMethod.POST, entity, String.class);
			String message = response.getBody();
			if (message != null) {
				Log.show(message);
			}
		} catch (Throwable t) {
			showErrorMsg(t);
		}
	}


	/**
	 * call rest service with "Delete" 
	 * @param object
	 */
	public <T> void callDeleteService(T object) {
		try {
			RestTemplate template = getRestTemplate();
			//template.delete(getCommandUrl());
			HttpEntity<T> entity = new HttpEntity<T>(object);
			ResponseEntity<String> response = template.exchange(getCommandUrl(), HttpMethod.DELETE, entity,
					String.class);
			String message = response.getBody();
			if (message != null) {
				Log.show(message);
			}
		} catch (Throwable t) {
			showErrorMsg(t);
		}
	}
	
	/**
	 * Download file from server.
	 */
	public void callDownloadFile(String fileName) {
		try {
			RestTemplate template = getRestTemplate();
			byte[] bytes = template.getForObject(getCommandUrl(), byte[].class);
			FileCopyUtils.copy(bytes, new File(fileName));
			Log.show("download file successfully. file name is:" + fileName);
		} catch (Throwable t) {
			showErrorMsg(t);
		}
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
			String serviceUrl = PropertyUtil.getTargetUrl();
			if (serviceUrl == null || serviceUrl.length() == 0) {
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
