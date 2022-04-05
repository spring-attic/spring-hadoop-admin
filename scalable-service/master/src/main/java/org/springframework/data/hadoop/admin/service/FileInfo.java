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


import java.io.Serializable;

/**
 * 
 * @author Jarred Li
 * 
 */
@SuppressWarnings("serial")
public class FileInfo implements Comparable<FileInfo>, Serializable {
	private final String path;

	private final String shortPath;

	private final boolean local;

	public FileInfo(String path) {
		this(path, true);
	}

	public FileInfo(String path, boolean local) {
		super();
		this.path = path.replace("\\", "/");
		this.shortPath = extractPath(path);
		this.local = local;
	}
	
	public FileInfo shortPath() {
		FileInfo info = new FileInfo(shortPath, local);
		return info;
	}
	
	public String getPattern() {
			return path;
	}

	private String extractPath(String path) {
		return path;
	}
	/**
	 * @return the local
	 */
	public boolean isLocal() {
		return local;
	}


	public String getPath() {
		return path;
	}

	public String getFileName() {
			return path;
	}

	public int compareTo(FileInfo o) {
		return shortPath.equals(o.shortPath) ? path.compareTo(o.path) : path.compareTo(o.path);
	}

	public String toString() {
		return "FileInfo [path=" + path + ", local=" + local + "]";
	}

}