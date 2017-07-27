package fr.openwide.alfresco.app.core.security.service;

import java.util.concurrent.Callable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

public interface RunAsUserManager {

	Optional<Authentication> getOriginalAuthentication();

	Optional<UserDetails> getOriginalUserDetails();

	Optional<String> getOriginalUsername();

	<T> T runAsUser(String username, Callable<T> task) throws Exception;
	void runAsUser(String username, Runnable task);

	<T> T runAsUser(UserDetails user, Callable<T> work) throws Exception;
	void runAsUser(UserDetails user, Runnable task);


}
