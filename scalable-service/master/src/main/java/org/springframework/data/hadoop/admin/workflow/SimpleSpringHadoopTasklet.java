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
package org.springframework.data.hadoop.admin.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * Simple Spring Batch Job wrapper if the uploaded Spring Hadoop descriptor is not Spring Batch Job.
 * 
 * @author Jarred Li
 *
 */
public class SimpleSpringHadoopTasklet implements Tasklet {

	private FileSystemXmlApplicationContext context;

	private static final Log logger = LogFactory.getLog(SimpleSpringHadoopTasklet.class);

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.step.tasklet.Tasklet#execute(org.springframework.batch.core.StepContribution, org.springframework.batch.core.scope.context.ChunkContext)
	 */
	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
		try {
			context.refresh();
		} catch (Throwable t) {
			logger.error("luanch Spring Hadoop job failed.", t);
		}
		return RepeatStatus.FINISHED;
	}

	/**
	 * @return the context
	 */
	public FileSystemXmlApplicationContext getContext() {
		return context;
	}

	/**
	 * @param context the context to set
	 */
	public void setContext(FileSystemXmlApplicationContext context) {
		this.context = context;
	}

}
