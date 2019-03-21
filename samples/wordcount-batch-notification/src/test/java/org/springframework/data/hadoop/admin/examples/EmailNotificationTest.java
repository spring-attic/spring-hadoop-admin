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
package org.springframework.data.hadoop.admin.examples;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author Jarred Li
 *
 */
@ContextConfiguration("/mail-context.xml")
@RunWith(SpringJUnit4ClassRunner.class)
public class EmailNotificationTest {

	@Autowired
	private ApplicationContext context;

	/**
	 * Test method for {@link org.springframework.data.hadoop.admin.examples.EmailNotification#afterJob(org.springframework.batch.core.JobExecution)}.
	 */
	@Test
	public void testAfterJob() {
		EmailNotification notification = context.getBean("emailNotification", EmailNotification.class);
		JobExecution jobExecution = new JobExecution(1l);
		jobExecution.setJobInstance(new JobInstance(null, null, "aaa"));
		notification.afterJob(jobExecution);
	}

}
