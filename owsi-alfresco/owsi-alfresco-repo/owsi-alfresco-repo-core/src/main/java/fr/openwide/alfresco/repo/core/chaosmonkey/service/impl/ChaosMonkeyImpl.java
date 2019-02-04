
package fr.openwide.alfresco.repo.core.chaosmonkey.service.impl;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ConcurrentModificationException;
import java.util.Random;

import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;

import fr.openwide.alfresco.repo.core.chaosmonkey.service.ChaosMonkey;

/**
 * Si l'on veut tester si un dév survit à une erreur aléatoire de concurrence, que le 
 * @seee {@link RetryingTransactionCallback} est bien en place, on utilise un Chaos Monkey
 * qui plante aléatoirement.
 * 
 * Il faut juste appeler chaosMonkey.test() à différents endroits du code à tester. 
 * Cela génére de manière aléatoire une exception.
 * 
 * @author asauvez
 */
public class ChaosMonkeyImpl implements InitializingBean, ChaosMonkey {

	private static final Logger LOG = LoggerFactory.getLogger(ChaosMonkey.class);

	private Random random;

	private double failOccurance = 0.0;
	private int maxRetries = 0;

	private Class<? extends Throwable> exception = ConcurrentModificationException.class;

	@Override
	public void test() {
		test(1.0);
	}

	@Override
	public void test(double factor) {
		if (failOccurance == 0.0) return;
		
		if (random.nextFloat() < failOccurance * factor) {
			throwUnchecked(ChaosMonkey.class.getName());
		}
	}
	
	@Override
	public void afterPropertiesSet() throws Exception {
		if (failOccurance != 0.0f) {
			LOG.info("failOccurance=" + failOccurance + ". server.transaction.max-retries=" + maxRetries);
		}
	}
	
	private void throwUnchecked(String msg) {
		LOG.warn(msg);
		Throwable throwable = getThrowable(msg);
		ChaosMonkeyImpl.<RuntimeException> throwsUnchecked(throwable);
	}
	
	private Throwable getThrowable(String msg) {
		try {
			Constructor<? extends Throwable> constructor;
			try {
				constructor = exception.getConstructor(String.class);
			} catch (NoSuchMethodException e) {
				constructor = exception.getConstructor();
			}
			return constructor.newInstance(msg);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
			throw new IllegalStateException(e);
		}
	}

	/** Hack pour lancer une exception checké sans la mettre dans le throws. */
	@SuppressWarnings("unchecked")
	private static <T extends Exception> void throwsUnchecked(Throwable throwable) throws T {
		throw (T) throwable;
	}

	public void setFailOccurance(double failOccurance) {
		this.failOccurance = failOccurance;
	}
	public void setSeed(long seed) {
		this.random = (seed != 0) ? new Random(seed) : new Random();
	}
	@SuppressWarnings("unchecked")
	public void setException(String exception) throws ClassNotFoundException {
		this.exception = (Class<? extends Throwable>) Class.forName(exception);
	}
	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}
}
