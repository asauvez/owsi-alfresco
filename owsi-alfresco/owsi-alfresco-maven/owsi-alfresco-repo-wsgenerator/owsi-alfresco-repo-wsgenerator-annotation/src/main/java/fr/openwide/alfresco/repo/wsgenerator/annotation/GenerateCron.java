package fr.openwide.alfresco.repo.wsgenerator.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Planifie une tâche.
 * Annotation à mettre sur une classe Runnable ou org.quartz.Job.
 * Il est nécessaire d'avoir owsi-alfresco-repo-core pour avoir la classe @see CronRunnableJob.
 * 
 * Exemple :
 * 
 * @GenerateCron(
	id = "acme.importInvoice.cron",
	cronExpression = "${acme.importInvoice.cron.expression}",
	enable = "${acme.importInvoice.cron.enabled:true}",
	logAsInfo = true
)
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface GenerateCron {
	
	String id() default "";

	/** Les beans Spring à lancer avant */
	String[] dependsOn() default {};

	/** L'expression cron. Peut être une variable ("${owsi.solraudit.cronExpression}")
	 * https://www.freeformatter.com/cron-expression-generator-quartz.html
	 */
	String cronExpression();
	
	/** Delail avant le démarrage. 
	 * En milisecond ou au format ISO-8601 ("PT1M");
	 */
	String startDelay() default "0";
	
	/** Activé ou non. 
	 * Peut être une variable ${owsi.solraudit.enabled:true}". */
	String enable() default "true";
	
	/** Une transaction est lancée autour de la tâche. */
	boolean readOnly() default false;
	
	/** La tâche se fait en tant que cet utilisateur. */
	String runAs() default "System";
	
	/** Une trace dans les logs sont faites au démarrage et à la fin de la tâche. 
	 * Indique si elle doit être fait en DEBUG (defaut) ou INFO. */
	boolean logAsInfo() default false;
}
