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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FsShell;
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
public class FsShellCommand implements CommandMarker {

	private FsShell shell;

	@CliCommand(value = "dfs", help = "run dfs commands")
	public void runDfsCommands(
			@CliOption(key = { "ls" }, mandatory = false, specifiedDefaultValue = ".", help = "directory to be listed") final String lsPath, 
			@CliOption(key = { "lsr" }, mandatory = false, specifiedDefaultValue = ".", help = "directory to be listed with recursion") final String lsrPath,
			@CliOption(key = { "cat" }, mandatory = false, help = "file to be showed") final String catFile,
			@CliOption(key = { "chgrp" }, mandatory = false, help = "file to be changed group") final String chgrpFile,
			@CliOption(key = { "chmod" }, mandatory = false, help = "file to be changed right") final String chmodFile,
			@CliOption(key = { "chown" }, mandatory = false, help = "file to be changed owner") final String chownFile,
			@CliOption(key = { "copyFromLocal" }, mandatory = false, help = "file to be copyied") final String copyFromLocalFile,
			@CliOption(key = { "copyToLocal" }, mandatory = false, help = "file to be copyied") final String copyToLocalFile,
			@CliOption(key = { "count" }, mandatory = false, help = "file to be count") final String countFile,
			@CliOption(key = { "cp" }, mandatory = false, help = "file to be copied") final String cpFile,
			@CliOption(key = { "du" }, mandatory = false, help = "display sizes of file") final String duFile,
			@CliOption(key = { "dus" }, mandatory = false, help = "display summary sizes of file") final String dusFile,
			@CliOption(key = { "expunge" }, mandatory = false, help = "empty the trash") final String expunge,
			@CliOption(key = { "get" }, mandatory = false, help = "copy to local") final String getFile,
			@CliOption(key = { "getmerge" }, mandatory = false, help = "merge file") final String getmergeFile,
			@CliOption(key = { "rm" }, mandatory = false, help = "remove file") final String rmFile,
			@CliOption(key = { "mkdir" }, mandatory = false, help = "create new directory") final String mkdirFile,
			@CliOption(key = { "tail" }, mandatory = false, help = "tail the file") final String tailFile,
			@CliOption(key = { "test" }, mandatory = false, help = "test a file") final String testFile
			) {
		try {
			setupShell();
		} catch (Exception e) {
			Log.error("run HDFS shell failed. " + e.getMessage());
		}
		
		if (lsPath != null) {
			runCommand("-ls",lsPath);
			return;
		}
		else if (lsrPath != null) {
			runCommand("-lsr",lsrPath);
			return;
		}
		else if (catFile != null) {
			runCommand("-cat",catFile);
			return;
		}
		else if (chgrpFile != null) {
			runCommand("-chgrp",chgrpFile);
			return;
		}
		else if (chmodFile != null) {
			runCommand("-chmod",chmodFile);
			return;
		}
		else if (chownFile != null) {
			runCommand("-chown",chownFile);
			return;
		}
		else if (copyFromLocalFile != null) {
			runCommand("-copyFromLocal",copyFromLocalFile);
			return;
		}
		else if (copyToLocalFile != null) {
			runCommand("-copyToLocal",copyToLocalFile);
			return;
		}
		else if (countFile != null) {
			runCommand("-count",countFile);
			return;
		}
		else if (cpFile != null) {
			runCommand("-cp",cpFile);
			return;
		}
		else if (duFile != null) {
			runCommand("-du",duFile);
			return;
		}
		else if (dusFile != null) {
			runCommand("-dus",dusFile);
			return;
		}
		else if (expunge != null) {
			runCommand("-expunge",expunge);
			return;
		}
		else if (getFile != null) {
			runCommand("-get",getFile);
			return;
		}
		else if (getmergeFile != null) {
			runCommand("-getmerge",getmergeFile);
			return;
		}
		else if (rmFile != null) {
			runCommand("-rm",rmFile);
			return;
		}
		else if (mkdirFile != null) {
			runCommand("-mkdir",mkdirFile);
			return;
		}
		else if (tailFile != null) {
			runCommand("-tail",tailFile);
			return;
		}
		else if (testFile != null) {
			runCommand("-test",testFile);
			return;
		}
	}

	/**
	 * @param value
	 */
	private void runCommand(String command, String value) {
		List<String> argv = new ArrayList<String>();
		argv.add(command);
		String[] fileNames = value.split(" ");
		argv.addAll(Arrays.asList(fileNames));
		try {
			shell.run(argv.toArray(new String[0]));
		} catch (Exception e) {
			Log.error("run HDFS shell failed. " + e.getMessage());
		}
	}

	private void setupShell() throws Exception {
		Configuration config = new Configuration();
		config.setStrings("fs.default.name", PropertyUtil.getDfsName());
		shell = new FsShell(config);
	}

}
