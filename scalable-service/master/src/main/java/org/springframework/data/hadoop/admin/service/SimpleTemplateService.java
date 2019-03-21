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
package org.springframework.data.hadoop.admin.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.data.hadoop.admin.util.Constant;

/**
 * @author Jarred Li
 *
 */
public class SimpleTemplateService extends BaseFileService implements TemplateService {
	{
		File outputDir = new File(System.getProperty("java.io.tmpdir", "/tmp"), Constant.TEMPLATE_LOCATION);
		setOutputDir(outputDir);
	}

	private static final Log logger = LogFactory.getLog(SimpleTemplateService.class);

	@Override
	public List<TemplateInfo> getTemplates(int start, int pageSize) throws IOException {
		List<TemplateInfo> templates = new ArrayList<TemplateInfo>();
		List<FileInfo> files = getFiles(start, pageSize);
		TemplateInfo ti;
		for (FileInfo file : files) {
			String fileName = file.getFileName();
			int index = fileName.lastIndexOf(".");
			fileName = fileName.substring(0,index);
			String[] elements = fileName.split("-");
			if (elements != null && elements.length == 3) {
				ti = new TemplateInfo();
				ti.setFileInfo(file);
				ti.setTemplateName(elements[0]);
				ti.setTemplateVersion(elements[2]);
				templates.add(ti);
			}
			else {
				logger.warn("invalid tempalte name");
			}
		}
		return new ArrayList<TemplateInfo>(templates.subList(start, Math.min(start + pageSize, templates.size())));
	}

}
