package fr.openwide.alfresco.repository.remote.authentication.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.TicketComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;
import fr.openwide.alfresco.repository.api.authentication.model.UserReference;
import fr.openwide.alfresco.repository.api.authentication.service.AuthenticationRemoteService;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.remote.exception.AccessDeniedRemoteException;

public class AuthenticationRemoteServiceImpl implements AuthenticationRemoteService {

	private AuthenticationService authenticationService;
	private TicketComponent ticketComponent;
	private PersonService personService;
	private NodeService nodeService;
	private AuthorityService authorityService;

	@Override
	public RepositoryUser authenticate(String username, String password) {
		try {
			authenticationService.authenticate(username, password.toCharArray());
			return getCurrentUser();
		} catch (AuthenticationException e) {
			throw new AccessDeniedRemoteException(e);
		} finally {
			AuthenticationUtil.clearCurrentSecurityContext();
		}
	}

	@Override
	public RepositoryUser getAuthenticatedUser() {
		// user should be pre-authenticated at this point
		return getCurrentUser();
	}

	private RepositoryUser getCurrentUser() {
		// build ticket
		RepositoryTicket ticket = new RepositoryTicket(authenticationService.getCurrentTicket());
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
		Map<QName, Serializable> userProps = nodeService.getProperties(userNodeRef);
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
		user.setFirstName((String) userProps.get(ContentModel.PROP_FIRSTNAME));
		user.setLastName((String) userProps.get(ContentModel.PROP_LASTNAME));
		user.setEmail((String) userProps.get(ContentModel.PROP_EMAIL));
		user.getAuthorities().addAll(Lists.transform(new ArrayList<>(userAuthorities), new Function<String, RepositoryAuthority>() {
			@Override
			public RepositoryAuthority apply(String autority) {
				return new RepositoryAuthority(autority);
			}
		}));
		user.setAdmin(userAdmin);
		return user;
	}

	/**
	 * {@see org.alfresco.repo.web.scripts.bean.LoginTicketDelete}
	 */
	@Override
	public void logout(RepositoryTicket ticket) throws AccessDeniedRemoteException {
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
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

}
