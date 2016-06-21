package fr.openwide.demo.share.servlet;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.auth.AuthenticationException;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.CredentialVaultProviderException;
import org.springframework.extensions.surf.exception.RequestContextException;
import org.springframework.extensions.surf.mvc.UrlViewController;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.connector.AlfrescoAuthenticator;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.ConnectorSession;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.CredentialsImpl;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.web.servlet.ModelAndView;

/**
 * Ticket-based controller to handle requests between Share and Alfresco. 
 * 
 * @author nbarithel
 *
 */
public class SlingshotApiController extends UrlViewController {
	public static final String SHARE_TICKET_HEADER_NAME = "X-Share-Ticket";
	// endpoint which validates alfresco ticket
	public static final String ALFRESCO_TICKET_ENDPOINT_ID = "alfresco-ticket";

	private ConnectorService connectorService;

	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) {
		Enumeration<String> i = request.getHeaderNames();
		System.out.println(i);
		String ticket = request.getHeader(SHARE_TICKET_HEADER_NAME);
		if (ticket != null && ! AuthenticationUtil.isAuthenticated(request)) {
			String endpoint = "alfresco"; // SlingshotUserFactory.ALFRESCO_ENDPOINT_ID;
			EndpointDescriptor descriptor = connectorService.getRemoteConfig().getEndpointDescriptor(endpoint);
			if (descriptor.getExternalAuth()) {
				endpoint = ALFRESCO_TICKET_ENDPOINT_ID;
			}
			// check ticket and retrieve username
			String username;
			try {
				username = retrieveUsername(endpoint, ticket);
			} catch (AuthenticationException e) {
				throw new IllegalStateException(e);
			}
			// build security context
			ConnectorSession connectorSession = connectorService.getConnectorSession(request.getSession(), endpoint);
			connectorSession.setParameter(AlfrescoAuthenticator.CS_PARAM_ALF_TICKET, ticket);
			try {
				Credentials credentials = new CredentialsImpl(endpoint);
				credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
				CredentialVault vault = connectorService.getCredentialVault(request.getSession(), username);
				vault.store(credentials);
			} catch (CredentialVaultProviderException e) {
				throw new IllegalStateException(e);
			}
			// set USER_ID into the session
			request.getSession().setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, username);
			// set endpoint to use before retrieving user info 
			RequestContext context = ThreadLocalRequestContext.getRequestContext();
			context.getAttributes().put(RequestContext.USER_ENDPOINT, endpoint);
			// force user retrival, this is required because webscript may have user authentication
			try {
				RequestContextUtil.initRequestContext(getWebApplicationContext(), request);
			} catch (RequestContextException e) {
				throw new IllegalStateException(e);
			}
		}
		return super.handleRequestInternal(request, response);
	}

	private String retrieveUsername(String endpoint, String ticket) throws AuthenticationException {
		Connector connector;
		try {
			connector = connectorService.getConnector(endpoint);
		} catch (ConnectorServiceException e) {
			throw new AuthenticationException("Could not get connector for endpoint: " + endpoint, e);
		}
		// build context
		Map<String, String> parameters = Collections.singletonMap("alf_ticket", ticket);
		ConnectorContext context = new ConnectorContext(HttpMethod.GET, parameters , null);
		Response response = connector.call("/owsi/authentication/username", context);
		// read back the ticket
		if (response.getStatus().getCode() != HttpServletResponse.SC_OK) {
			throw new AuthenticationException("Unable to validate ticket with Alfresco");
		} else {
			return response.getResponse();
		}
	}

	public void setConnectorService(ConnectorService connectorService) {
		this.connectorService = connectorService;
	}
}
