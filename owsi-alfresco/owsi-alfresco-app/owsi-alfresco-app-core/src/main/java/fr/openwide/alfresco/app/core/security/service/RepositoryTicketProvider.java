package fr.openwide.alfresco.app.core.security.service;

import fr.openwide.alfresco.api.core.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.api.core.authentication.model.RepositoryUser;

public interface RepositoryTicketProvider {

	RepositoryTicket getTicket();

	RepositoryUser getTicketOwner();

}
