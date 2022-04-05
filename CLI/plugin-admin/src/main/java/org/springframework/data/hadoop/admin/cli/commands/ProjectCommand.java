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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.data.hadoop.admin.cli.util.Log;
import org.springframework.shell.core.CommandMarker;
import org.springframework.shell.core.annotation.CliCommand;
import org.springframework.shell.core.annotation.CliOption;
import org.springframework.stereotype.Component;

/**
 * @author Jarred Li
 *
 */
@Component
public class ProjectCommand implements CommandMarker {

	@CliCommand(value = "project new", help = "create new project")
	public void createNewProject(@CliOption(key = { "type" }, mandatory = true, help = "project type to be created") final ProjectType type, @CliOption(key = { "name" }, mandatory = true, help = "project name") final String name) {
		String templateFileName = downloadTemplateFromServer(type);
		if (templateFileName != null) {
			unzipTemplate(templateFileName, name);
		}
	}



	/**
	 * @param templateFileName
	 * @param name
	 * @throws IOException 
	 */
	private void unzipTemplate(String templateFileName, String name) {
		File dir = new File(name);
		dir.mkdirs();
		ZipInputStream zis = null;
		BufferedOutputStream dest = null;
		try {
			FileInputStream fis = new FileInputStream(templateFileName);
			zis = new ZipInputStream(new BufferedInputStream(fis));
			ZipEntry entry;
			while ((entry = zis.getNextEntry()) != null) {
				byte[] buffer = new byte[4096];
				File file = new File(dir, entry.getName());
				new File(file.getParent()).mkdirs();
				if (entry.isDirectory()) {
					continue;
				}
				FileOutputStream fos = new FileOutputStream(file);
				dest = new BufferedOutputStream(fos);
				int count;
				while ((count = zis.read(buffer)) != -1) {
					dest.write(buffer, 0, count);
				}
				dest.flush();
			}
		} catch (Exception e) {
			Log.error("unzip template failed." + e.getMessage());
		} finally {
			try {
				if (dest != null) {
					dest.close();
				}
				if (zis != null) {
					zis.close();
				}
			} catch (IOException e) {
				Log.error("unzip template failed." + e.getMessage());
			}
		}
	}

	public String downloadTemplateFromServer(final ProjectType type) {
		String result = null;
		BaseCommand baseCmd = new BaseCommand();
		baseCmd.setCommandURL("templates.json");
		String json = baseCmd.getJson();
		ObjectMapper mapper = new ObjectMapper();
		TemplateList templateList = null;
		try {
			templateList = mapper.readValue(json, TemplateList.class);
		} catch (Exception e) {
			Log.error("read template information failed." + e.getMessage());
		}
		if (templateList != null) {
			String path = null;
			for (TemplateList.Templates.Uploaded u : templateList.getTemplates().getUploaded()) {
				if (u.getName().toLowerCase().startsWith(type.name().toLowerCase())) {
					path = u.getPath();
					break;
				}
			}
			if (path != null) {
				String fileName = path.substring(path.lastIndexOf("/") + 1);
				String url = "templates/" + path;
				baseCmd.setCommandURL(url);
				baseCmd.callDownloadFile(fileName);
				result = fileName;
			}
			else {
				Log.error("can't find the template from server");
			}
		}
		return result;
	}

	public static class TemplateList {
		private Templates templates;

		/**
		 * @return the templates
		 */
		public Templates getTemplates() {
			return templates;
		}

		/**
		 * @param templates the templates to set
		 */
		public void setTemplates(Templates templates) {
			this.templates = templates;
		}

		public static class Templates {
			private String resource;


			private Uploaded[] uploaded;

			/**
			 * @return the resource
			 */
			public String getResource() {
				return resource;
			}

			/**
			 * @param resoruce the resource to set
			 */
			public void setResource(String resource) {
				this.resource = resource;
			}

			/**
			 * @return the uploaded
			 */
			public Uploaded[] getUploaded() {
				return uploaded;
			}

			/**
			 * @param uploaded the uploaded to set
			 */
			public void setUploaded(Uploaded[] uploaded) {
				this.uploaded = uploaded;
			}


			public static class Uploaded {
				private String name;
				private String version;
				private String resource;
				private String path;

				/**
				 * @return the name
				 */
				public String getName() {
					return name;
				}

				/**
				 * @param name the name to set
				 */
				public void setName(String name) {
					this.name = name;
				}

				/**
				 * @return the version
				 */
				public String getVersion() {
					return version;
				}

				/**
				 * @param version the version to set
				 */
				public void setVersion(String version) {
					this.version = version;
				}

				/**
				 * @return the resource
				 */
				public String getResource() {
					return resource;
				}

				/**
				 * @param resource the resource to set
				 */
				public void setResource(String resource) {
					this.resource = resource;
				}

				/**
				 * @return the path
				 */
				public String getPath() {
					return path;
				}

				/**
				 * @param path the path to set
				 */
				public void setPath(String path) {
					this.path = path;
				}

			}
		}
	}

}
