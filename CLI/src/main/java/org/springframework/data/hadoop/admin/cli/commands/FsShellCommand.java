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

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.data.hadoop.admin.cli.util.Log;
import org.springframework.data.hadoop.admin.cli.util.PropertyUtil;
import org.springframework.roo.shell.CliCommand;
import org.springframework.roo.shell.CliOption;
import org.springframework.roo.shell.CommandMarker;
import org.springframework.stereotype.Component;

/**
 * @author Jarred Li
 *
 */
@Component
public class FsShellCommand implements CommandMarker, InitializingBean{

	private Configuration config;

	private FsShell shell;

	@CliCommand(value = "dfs", help = "run dfs commands")
	public void runDfsCommands(@CliOption(key = { "ls" }, mandatory = false, specifiedDefaultValue=".", help = "directory to be listed") final String path) {
		String[] argv = null;
		try {
			if (path != null && !path.equals(".")) {
				argv = new String[2];
				argv[0] = "-ls";
				argv[1] = path;
			}
			else {
				argv = new String[1];
				argv[0] = "-ls";
			}
			shell.run(argv);
		} catch (Exception e) {
			Log.error("run HDFS shell failed. " + e.getMessage());
		}
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		config = new Configuration();
		config.setStrings("fs.default.name", PropertyUtil.getDfsName());		
		shell = new FsShell(config);
	}

}
