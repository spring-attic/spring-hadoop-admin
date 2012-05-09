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

import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * @author Jarred Li
 *
 */
@Component
public class JobsCommand extends BaseCommand implements CommandMarker {
	
	private static int count = 0;
	
	/**
	 * list all jobs
	 */
	@CliCommand(value = "jobs-all", help = "list all jobs information")
	public void getJobs() {
		super.setCommandURL("jobs.json");
		super.callGetService();
	}
	
	/**
	 * list job by name
	 * 
	 * @param jobName
	 */
	@CliCommand(value = "jobs-by-name", help = "list jobs information by name")
	public void getJobsByName(@CliOption(key = { "jobName" }, mandatory = true, help = "Job Name") final String jobName) {
		String url = "jobs/";
		url += jobName;
		url += ".json";
		super.setCommandURL(url);
		super.callGetService();
	}
	
	/**
	 * launch job 
	 * 
	 * @param jobName
	 */
	@CliCommand(value = "launch-job", help = "execute job")
	public void executeJob(@CliOption(key = { "jobName" }, mandatory = true, help = "Job Name") final String jobName) {
		String url = "jobs/";
		url += jobName;
		url += ".json";
		super.setCommandURL(url);
		MultiValueMap<String, String> mvm = new LinkedMultiValueMap<String, String>();
		mvm.add("jobParameters", "fail=false, id=" + count++);
		super.callPostService(mvm);
	}

}
