package fr.openwide.alfresco.app.core.security.service;

import java.util.concurrent.Callable;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import com.google.common.base.Optional;

public interface RunAsUserManager {

	Optional<Authentication> getOriginalAuthentication();

	Optional<UserDetails> getOriginalUserDetails();

	Optional<String> getOriginalUsername();

	<T> T runAsUser(String username, Callable<T> task) throws Exception;

}
