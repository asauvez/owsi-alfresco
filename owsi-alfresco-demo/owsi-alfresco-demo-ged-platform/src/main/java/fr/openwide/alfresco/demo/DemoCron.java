package fr.openwide.alfresco.demo;

import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateCron;

@GenerateCron(
		id = "owsi.demo.cron",
		cronExpression = "${owsi.demo.cronExpression:0 * * ? * *}",
		enable = "${owsi.solraudit.enabled:true}",
		logAsInfo = true
	)
public class DemoCron implements Runnable {

	@Override
	public void run() {
		System.out.println("I am Cron.");
	}
}
