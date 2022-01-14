package fr.openwide.alfresco.repo.core.cron.model;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.schedule.AbstractScheduledLockedJob;
import org.alfresco.service.transaction.TransactionService;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CronRunnableJob extends AbstractScheduledLockedJob {
	
	@Override
	public void executeJob(JobExecutionContext jobContext) throws JobExecutionException {
		JobDataMap jobData = jobContext.getJobDetail().getJobDataMap();
		Object runnable = jobData.get("runnable");
		Logger logger = LoggerFactory.getLogger(runnable.getClass());
		TransactionService transactionService = (TransactionService) jobData.get("transactionService");
		boolean readOnly = Boolean.parseBoolean((String) jobData.get("readOnly"));
		boolean logAsInfo = Boolean.parseBoolean((String) jobData.get("logAsInfo"));
		String runAs = (String) jobData.get("runAs");

		boolean enable = Boolean.parseBoolean((String) jobData.get("enable"));
		if (! enable) {
			logger.warn(runnable.getClass().getName() + " is disabled.");
			return;
		}
		
		if (logAsInfo) {
			logger.info( ">>> Scheduled start");
		} else {
			logger.debug(">>> Scheduled start");
		}
		
		try {
			AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>() {
				@Override
				public Void doWork() throws Exception {
					transactionService.getRetryingTransactionHelper().doInTransaction(
						new RetryingTransactionCallback<Void>() {
							@Override
							public Void execute() throws Throwable {
								if (runnable instanceof Runnable) {
									((Runnable) runnable).run();
								} else if (runnable instanceof Job) {
									((Job) runnable).execute(jobContext);
								} else {
									throw new IllegalStateException("Cron should extend type java.lang.Runnable or org.quartz.Job, not " + runnable.getClass());
								}
								return null;
							}
						}, readOnly, true);
					return null;
				}
			}, runAs);

			if (logAsInfo) {
				logger.info( "<<< Schedule end");
			} else {
				logger.debug("<<< Schedule end");
			}
		} catch (Exception ex) {
			logger.error(runnable.getClass().getName(), ex);
			throw new IllegalStateException(ex);
		}
	}
}
