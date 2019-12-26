package fr.openwide.alfresco.repo.remote.authentication.service.impl;

import java.util.ArrayList;
import java.util.Set;

import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.TicketComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PersonService;
import org.springframework.beans.factory.InitializingBean;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;
import fr.openwide.alfresco.api.core.authentication.model.TicketReference;
import fr.openwide.alfresco.api.core.authentication.model.UserReference;
import fr.openwide.alfresco.api.core.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.exception.AccessDeniedRemoteException;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class AuthenticationRemoteServiceImpl implements AuthenticationRemoteService, InitializingBean {

	private AuthenticationService authenticationService;
	private TicketComponent ticketComponent;
	private PersonService personService;
	private NodeRemoteService nodeRemoteService;
	private AuthorityService authorityService;
	private ConversionService conversionService;

	@Override
	public void afterPropertiesSet() throws Exception {
//		URL url = getClass().getResource("/alfresco/extension/templates/webscripts/owsi/authentication/request.post.desc.xml");
//		if (url == null) {
//			throw new IllegalStateException("Les Webscripts n'ont pas été générés automatiquement. "
//				+ "Vous devez lancer un mvn package sur le projet owsi-alfresco-repo-core, puis le raffraichir dans Eclipse.");
//		}
	}
	
	@Override
	public RepositoryUser authenticate(String username, String password, NodeScope nodeScope) {
		try {
			authenticationService.authenticate(username, password.toCharArray());
			return getCurrentUser(nodeScope);
		} catch (AuthenticationException e) {
			throw new AccessDeniedRemoteException(e);
		} finally {
			AuthenticationUtil.clearCurrentSecurityContext();
		}
	}

	@Override
	public RepositoryUser getAuthenticatedUser(NodeScope nodeScope) {
		// user should be pre-authenticated at this point
		return getCurrentUser(nodeScope);
	}

	private RepositoryUser getCurrentUser(NodeScope nodeScope) {
		// build ticket
		TicketReference ticket = new TicketReference(authenticationService.getCurrentTicket());
		final String userTicket = ticketComponent.getAuthorityForTicket(ticket.getTicket());
		UserReference userReference = new UserReference(userTicket);
		if (! AuthenticationUtil.getFullyAuthenticatedUser().equals(userTicket)) {
			// do not go any further if users are different
			throw new AccessDeniedRemoteException();
		}
		
		// get person
		NodeRef userNodeRef;
		try {
			userNodeRef = personService.getPerson(userReference.getUsername());
		} catch (NoSuchPersonException e) {
			throw new AccessDeniedRemoteException(e);
		}
		RepositoryNode userNode = nodeRemoteService.get(conversionService.get(userNodeRef), nodeScope);
		
		// get user authorities
		Set<String> userAuthorities = AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Set<String>>() {
			@Override
			public Set<String> doWork() throws Exception {
				return authorityService.getAuthoritiesForUser(userTicket);
			}
		}, AuthenticationUtil.getSystemUserName());
		
		
		// get user is admin
		boolean userAdmin = authorityService.isAdminAuthority(userReference.getUsername());
		// build user
		RepositoryUser user = new RepositoryUser();
		user.setUserReference(userReference);
		user.setTicket(ticket);
		user.setUserNode(userNode);
		user.getAuthorities().addAll(Lists.transform(new ArrayList<>(userAuthorities), new Function<String, AuthorityReference>() {
			@Override
			public AuthorityReference apply(String autority) {
				return AuthorityReference.authority(autority);
			}
		}));
		user.setAdmin(userAdmin);
		return user;
	}

	@Override
	public String getAuthenticatedUsername() {
		// build ticket
		TicketReference ticket = new TicketReference(authenticationService.getCurrentTicket());
		String userTicket = ticketComponent.getAuthorityForTicket(ticket.getTicket());
		UserReference userReference = new UserReference(userTicket);
		if (! AuthenticationUtil.getFullyAuthenticatedUser().equals(userTicket)) {
			// do not go any further if users are different
			throw new AccessDeniedRemoteException();
		}
		return userReference.getUsername();
	}
	
	/**
	 * {@see org.alfresco.repo.web.scripts.bean.LoginTicketDelete}
	 */
	@Override
	public void logout(TicketReference ticket) throws AccessDeniedRemoteException {
		String ticketUser;
		try {
			ticketUser = ticketComponent.validateTicket(ticket.getTicket());
		} catch (AuthenticationException e) {
			throw new AccessDeniedRemoteException(e);
		}
		// do not go any further if tickets are different
		if (! AuthenticationUtil.getFullyAuthenticatedUser().equals(ticketUser)) {
			throw new AccessDeniedRemoteException();
		}
		authenticationService.invalidateTicket(ticket.getTicket());
	}

	public void setAuthenticationService(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	public void setTicketComponent(TicketComponent ticketComponent) {
		this.ticketComponent = ticketComponent;
	}
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	public void setNodeRemoteService(NodeRemoteService nodeRemoteService) {
		this.nodeRemoteService = nodeRemoteService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

}
