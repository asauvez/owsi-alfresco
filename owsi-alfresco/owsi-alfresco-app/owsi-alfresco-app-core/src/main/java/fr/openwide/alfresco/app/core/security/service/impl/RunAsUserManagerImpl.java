package fr.openwide.alfresco.app.core.security.service.impl;

import java.util.Collection;
import java.util.concurrent.Callable;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.intercept.RunAsManagerImpl;
import org.springframework.security.access.intercept.RunAsUserToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import com.google.common.base.Optional;

import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.core.jpa.security.runas.CoreRunAsManagerImpl;

public class RunAsUserManagerImpl extends RunAsManagerImpl implements RunAsUserManager {

	private UserService userService;
	private UserDetailsService userDetailsService;
	private AuthenticationManager authenticationManager;

	public RunAsUserManagerImpl(AuthenticationManager authenticationManager, UserDetailsService userDetailsService, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.userService = userService;
	}

	@Override
	public Optional<Authentication> getOriginalAuthentication() {
		Authentication authentication = userService.getCurrentAuthentication().orNull();
		if (authentication instanceof RunAsUserToken) {
			authentication = (Authentication) ((RunAsUserToken) authentication).getCredentials();
		}
		return Optional.fromNullable(authentication);
	}

	@Override
	public Optional<UserDetails> getOriginalUserDetails() {
		return userService.getUserDetails(getOriginalAuthentication().orNull());
	}

	@Override
	public Optional<String> getOriginalUsername() {
		Optional<UserDetails> current = getOriginalUserDetails();
		return (current.isPresent()) ? Optional.of(current.get().getUsername()) : Optional.<String>absent();
	}

	@Override
	public Authentication buildRunAs(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
		Optional<Authentication> originalAuthentication = userService.getCurrentAuthentication();
		String username = null;
		for (ConfigAttribute attribute : attributes) {
			if (this.supports(attribute)) {
				username = attribute.getAttribute().replaceFirst(CoreRunAsManagerImpl.RUN_AS_PREFIX, "");
				break;
			}
		}
		if (username == null) {
			throw new IllegalStateException("Could not find a username attribute with prefix: " + CoreRunAsManagerImpl.RUN_AS_PREFIX);
		}
		UserDetails user = userDetailsService.loadUserByUsername(username);
		return buildRunAs(user, originalAuthentication);
	}

	protected RunAsUserToken buildRunAs(UserDetails user, Optional<Authentication> originalAuthentication) {
		Class<? extends Authentication> original = (originalAuthentication.isPresent()) ? originalAuthentication.get().getClass() : null;
		return new RunAsUserToken(getKey(), user, originalAuthentication.orNull(), user.getAuthorities(), original);
	}

	@Override
	public <T> T runAsUser(String username, Callable<T> work) throws Exception {
		Optional<Authentication> originalAuthentication = userService.getCurrentAuthentication();
		if (originalAuthentication.isPresent() && username.equals(originalAuthentication.get().getName())) {
			// No runAs if the current authenticated user is the target user
			return work.call();
		}
		UserDetails user = userDetailsService.loadUserByUsername(username);
		return runAsUser(user, work);
	}

	@Override
	public <T> T runAsUser(UserDetails user, Callable<T> work) throws Exception {
		Optional<Authentication> originalAuthentication = userService.getCurrentAuthentication();
		// This acts in fact as a Filter, building token and passing it to authenticationManager before setting the context
		Authentication runAsAuthentication = buildRunAs(user, originalAuthentication);
		authenticationManager.authenticate(runAsAuthentication);
		SecurityContextHolder.setContext(SecurityContextHolder.createEmptyContext());
		SecurityContextHolder.getContext().setAuthentication(runAsAuthentication);
		try {
			return work.call();
		} finally {
			// Crucial restore of SecurityContextHolder contents - do this before anything else.
			SecurityContextHolder.getContext().setAuthentication(originalAuthentication.orNull());
			afterRunAs(runAsAuthentication);
		}
	}

	protected void afterRunAs(@SuppressWarnings("unused") Authentication runAsAuthentication) {
		// may be overriden
	}

}
