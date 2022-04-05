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
package org.springframework.data.hadoop.admin.cli.commands;

import org.apache.commons.configuration.ConfigurationException;
import org.springframework.data.hadoop.admin.cli.util.Log;
import org.springframework.data.hadoop.admin.cli.util.PropertyUtil;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * Target command to set service URL
 * 
 * @author Jarred Li
 *
 */
@Component
public class TargetCommand implements CommandMarker {

	/**
	 * set Spring Hadoop Admin service URL
	 * 
	 * @param url spring hadoop service URL. For example, "http://localhost:8080/spring-hadoop-admin"
	 */
	@CliCommand(value = "service target", help = "connect to Spring Hadoop Admin server")
	public void target(@CliOption(key = { "url" }, mandatory = true, help = "Spring Hadoop Admin service URL") final String url) {
		try {
			PropertyUtil.setTargetUrl(url);
		} catch (ConfigurationException e) {
			Log.error("set target url failed. " + e.getMessage());
		}
	}
	
}
