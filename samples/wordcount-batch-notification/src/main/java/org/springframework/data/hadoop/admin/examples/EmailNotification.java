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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.Assert;

/**
 * @author Jarred Li
 *
 */
public class EmailNotification implements JobExecutionListener, InitializingBean {

	private Log logger = LogFactory.getLog(EmailNotification.class);

	private JavaMailSender mailSender;

	private SimpleMailMessage templateMessage;


	/**
	 * @param templateMessage the templateMessage to set
	 */
	public void setTemplateMessage(SimpleMailMessage templateMessage) {
		this.templateMessage = templateMessage;
	}

	/**
	 * @param mailSender the mailSender to set
	 */
	public void setMailSender(JavaMailSender mailSender) {
		this.mailSender = mailSender;
	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.JobExecutionListener#beforeJob(org.springframework.batch.core.JobExecution)
	 */
	@Override
	public void beforeJob(JobExecution jobExecution) {

	}

	/* (non-Javadoc)
	 * @see org.springframework.batch.core.JobExecutionListener#afterJob(org.springframework.batch.core.JobExecution)
	 */
	@Override
	public void afterJob(JobExecution jobExecution) {
		logger.info("afterJob enter");
		SimpleMailMessage message = new SimpleMailMessage(templateMessage);
		message.setSubject("Spring Batch Job Status");
		message.setText("Job " + jobExecution.getJobInstance().getJobName() + " completed. Status is: "
				+ jobExecution.getStatus());
		try {
			mailSender.send(message);
		} catch (Throwable t) {
			logger.error("send mail failed", t);
		}
		logger.info("sent mail");
	}

	/* (non-Javadoc)
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(mailSender, "mail sender must be set");
		Assert.notNull(this.templateMessage, "template message must be set");


	}

}
