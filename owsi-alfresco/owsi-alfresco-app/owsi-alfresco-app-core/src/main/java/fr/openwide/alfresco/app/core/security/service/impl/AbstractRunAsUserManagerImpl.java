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

import com.google.common.base.Optional;

import fr.openwide.alfresco.app.core.security.service.RunAsUserManager;
import fr.openwide.alfresco.app.core.security.service.UserService;
import fr.openwide.core.jpa.security.runas.CoreRunAsManagerImpl;

public abstract class AbstractRunAsUserManagerImpl extends RunAsManagerImpl implements RunAsUserManager {

	private UserService userService;
	private AuthenticationManager authenticationManager;

	public AbstractRunAsUserManagerImpl(AuthenticationManager authenticationManager, UserService userService) {
		this.authenticationManager = authenticationManager;
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
		return buildRunAs(username, originalAuthentication);
	}

	protected RunAsUserToken buildRunAs(String username, Optional<Authentication> originalAuthentication) {
		UserDetails user = loadUserDetails(username);
		Class<? extends Authentication> original = (originalAuthentication.isPresent()) ? originalAuthentication.get().getClass() : null;
		return new RunAsUserToken(getKey(), user, originalAuthentication.get(), user.getAuthorities(), original);
	}

	protected abstract UserDetails loadUserDetails(String username);

	@Override
	public <T> T runAsUser(String username, Callable<T> work) throws Exception {
		Optional<Authentication> originalAuthentication = userService.getCurrentAuthentication();
		if (originalAuthentication.isPresent() && username.equals(originalAuthentication.get().getName())) {
			// No runAs if the current authenticated user is the target user
			return work.call();
		} else {
			// This acts in fact as a Filter, building token and passing it to authenticationManager before setting the context
			Authentication runAsAuthentication = buildRunAs(username, originalAuthentication);
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
	}

	protected abstract void afterRunAs(Authentication runAsAuthentication);

}
