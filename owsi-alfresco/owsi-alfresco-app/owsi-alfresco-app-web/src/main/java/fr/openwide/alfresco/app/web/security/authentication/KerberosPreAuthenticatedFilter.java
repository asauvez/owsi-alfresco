package fr.openwide.alfresco.app.web.security.authentication;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.security.auth.Subject;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.jlan.server.auth.kerberos.KerberosDetails;
import org.alfresco.jlan.server.auth.kerberos.SessionSetupPrivilegedAction;
import org.alfresco.jlan.server.auth.ntlm.NTLM;
import org.alfresco.jlan.server.auth.spnego.NegTokenInit;
import org.alfresco.jlan.server.auth.spnego.OID;
import org.alfresco.jlan.server.auth.spnego.SPNEGO;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.filter.GenericFilterBean;

@Component("kerberosPreAuthenticatedFilter")
public class KerberosPreAuthenticatedFilter extends GenericFilterBean implements ApplicationEventPublisherAware {

	private static final Logger logger = LoggerFactory.getLogger(KerberosPreAuthenticatedFilter.class);

	private static final String AUTHORIZATION = "Authorization";

	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private Environment environment;

	private boolean enabled;
	private String configEntryName;
	private String bypassUriPattern;

	private boolean continueFilterChainOnUnsuccessfulAuthentication = true;

	private ApplicationEventPublisher eventPublisher;

	private String serviceAccountName;
	private Subject privilegedSubject;

	@PostConstruct
	public void serviceAuthentication() {
		enabled = environment.getRequiredProperty("jaas.login.enabled", Boolean.class);
		if (enabled) {
			configEntryName = environment.getRequiredProperty("jaas.login.configuration");
			bypassUriPattern = environment.getProperty("jaas.login.bypass.uriPattern");
			try {
				LoginContext loginContext = new LoginContext(configEntryName);
				loginContext.login();
				logger.info("Kerberos Preauthentification enabled using configuration: " + configEntryName);
				
				serviceAccountName = loginContext.getSubject().getPrincipals().iterator().next().getName();
				logger.info("Kerberos Preauthentification enabled using service account: " + serviceAccountName);
				
				this.privilegedSubject = loginContext.getSubject();
			} catch (LoginException e) {
				throw new IllegalStateException("Could not authenticate using Kerberos", e);
			} catch (SecurityException e) {
				throw new IllegalStateException("Could not load Kerberos configuration", e);
			}
		} else {
			logger.warn("Kerberos Preauthentification disabled");
		}
	}

	@Override
	public void doFilter(ServletRequest rq, ServletResponse rs, FilterChain chain) 
			throws IOException, ServletException {
		if (enabled && rq instanceof HttpServletRequest && rs instanceof HttpServletResponse) {
			if (logger.isDebugEnabled()) {
				logger.debug("Checking secure context token: " + SecurityContextHolder.getContext().getAuthentication());
			}
			HttpServletRequest request = (HttpServletRequest) rq;
			if (requiresAuthentication(request)) {
				HttpServletResponse response = (HttpServletResponse) rs;
				// Try to authenticate a pre-authenticated user with Spring Security if the user has not yet been authenticated
				boolean authenticated = doAuthenticate(request, response);
				if (! authenticated) {
					return; // stop the request from going further
				}
			}
		}
		chain.doFilter(rq, rs);
	}

	private boolean doAuthenticate(HttpServletRequest request, HttpServletResponse response) {
		Authentication authResult = null;
		Object principal = getPreAuthenticatedPrincipal(request, response);
		if (principal == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("No pre-authenticated principal found in request");
			}
			return false;
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Trying to authenticate with preauthenticated principal: " + principal);
		}
		try {
			PreAuthenticatedAuthenticationToken authRequest = new PreAuthenticatedAuthenticationToken(principal, "Kerberos");
			authResult = authenticationManager.authenticate(authRequest);
			successfulAuthentication(request, response, authResult);
		} catch (AuthenticationException failed) {
			unsuccessfulAuthentication(request, response, failed);
			if (! continueFilterChainOnUnsuccessfulAuthentication) {
				throw failed;
			}
		}
		return true;
	}

	public Object getPreAuthenticatedPrincipal(HttpServletRequest request, HttpServletResponse response) {
		String authHeader = request.getHeader(AUTHORIZATION);
		if (authHeader != null) {
			// Check for a Kerberos/SPNEGO authorization header
			if (authHeader.startsWith("Negotiate")) {
				final byte[] spnegoByts = Base64.decodeBase64(authHeader.substring(10).getBytes());
				if (isNTLMSSPBlob(spnegoByts, 0)) {
					if (logger.isDebugEnabled()) {
						logger.debug("Client sent an NTLMSSP security blob");
					}
					// Restart the authentication
					restartLoginChallenge(response);
					return null;
				}
				// Check the received SPNEGO token type
				int tokType = -1;
				try {
					tokType = SPNEGO.checkTokenType(spnegoByts, 0, spnegoByts.length);
				} catch (IOException e) {
					// Log the error
					logger.error("Could not check token type of SPNEGO", e);
				}
				// Check for a NegTokenInit blob
				if (tokType == SPNEGO.NegTokenInit) {
					// Parse the SPNEGO security blob to get the Kerberos ticket
					NegTokenInit negToken = new NegTokenInit();
					try {
						// Decode the security blob
						negToken.decode(spnegoByts, 0, spnegoByts.length);
						// Determine the authentication mechanism the client is using and logon
						String oidStr = null;
						if (negToken.numberOfOids() > 0) {
							oidStr = negToken.getOidAt(0).toString();
						}
						if (oidStr != null && (oidStr.equals(OID.ID_MSKERBEROS5) || oidStr.equals(OID.ID_KERBEROS5))) {
							// Kerberos logon
							SessionSetupPrivilegedAction sessSetupAction = new SessionSetupPrivilegedAction(serviceAccountName, negToken.getMechtoken());
							@SuppressWarnings("unchecked")
							Object result = Subject.doAs(privilegedSubject, sessSetupAction);
							if (result != null) {
								// Access the Kerberos response
								String username = ((KerberosDetails) result).getUserName();
								logger.info("Successfully logged on principal using Kerberos: " + username);
								return username;
							} else {
								// Send back a request for SPNEGO authentication
								restartLoginChallenge(response);
								return null;
							}
						}
					}
					catch (IOException e) {
						// Log the error
						logger.error("Could not decode the SPNEGO", e);
					}
				} else {
					//  Unknown SPNEGO token type
					if (logger.isDebugEnabled()) {
						logger.debug("Unknown SPNEGO token type");
					}
					// Send back a request for SPNEGO authentication
					restartLoginChallenge(response);
					return null;
				}
			} else if (authHeader.startsWith("NTLM")) {
				if (logger.isDebugEnabled()) {
					logger.debug("Received NTLM logon from client");
				}
				// Restart the authentication
				restartLoginChallenge(response);
				return null;
			}
		} else {
			restartLoginChallenge(response);
			return null;
		}
		return null;
	}

	/**
	 * Restart the Kerberos logon process
	 */
	protected void restartLoginChallenge(HttpServletResponse resp) {
		// Force the logon to start again
		resp.setHeader("WWW-Authenticate", "Negotiate");
		resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		try {
			resp.flushBuffer();
		} catch (IOException e) {
			logger.error("Cannot flush response buffer", e);
		}
	}

	protected boolean requiresAuthentication(HttpServletRequest request) {
		if (StringUtils.isNotEmpty(bypassUriPattern) && PatternMatchUtils.simpleMatch(bypassUriPattern, request.getServletPath())) {
			if (logger.isDebugEnabled()) {
				logger.debug("No authentication required for path: " + request.getServletPath());
			}
			return false;
		}
		Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
		if (currentUser == null) {
			return true;
		}
		return false;
	}

	/**
	 * Puts the <code>Authentication</code> instance returned by the authentication manager into the secure context
	 */
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, Authentication authResult) {
		if (logger.isDebugEnabled()) {
			logger.debug("Authentication success: " + authResult);
		}
		SecurityContextHolder.getContext().setAuthentication(authResult);
		// Fire event
		if (this.eventPublisher != null) {
			eventPublisher.publishEvent(new InteractiveAuthenticationSuccessEvent(authResult, this.getClass()));
		}
	}

	/**
	 * Ensures the authentication object in the secure context is set to null when authentication fails.
	 */
	protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
		SecurityContextHolder.clearContext();
		if (logger.isDebugEnabled()) {
			logger.debug("Cleared security context due to exception", failed);
		}
		request.getSession().setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION, failed);
	}

	/**
	 * Check if a security blob starts with the NTLMSSP signature
	 */
	protected final boolean isNTLMSSPBlob(byte[] bytes, int offset) {
		// Check if the blob has the NTLMSSP signature
		boolean isNTLMSSP = false;
		if ((bytes.length - offset) >= NTLM.Signature.length) {
			// Check for the NTLMSSP signature
			int idx = 0;
			while (idx < NTLM.Signature.length && bytes[offset + idx] == NTLM.Signature[idx]) {
				idx++;
			}
			if (idx == NTLM.Signature.length) {
				isNTLMSSP = true;
			}
		}
		return isNTLMSSP;
	}

	@Override
	public void setApplicationEventPublisher(ApplicationEventPublisher anApplicationEventPublisher) {
		this.eventPublisher = anApplicationEventPublisher;
	}

	public void setContinueFilterChainOnUnsuccessfulAuthentication(boolean shouldContinue) {
		continueFilterChainOnUnsuccessfulAuthentication = shouldContinue;
	}

}
