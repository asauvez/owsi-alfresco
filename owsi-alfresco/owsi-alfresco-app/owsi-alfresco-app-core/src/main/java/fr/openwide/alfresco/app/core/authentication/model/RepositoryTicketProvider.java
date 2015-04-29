package fr.openwide.alfresco.app.core.authentication.model;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;
import fr.openwide.alfresco.repository.api.authentication.model.RepositoryUser;

public interface RepositoryTicketProvider {

	RepositoryTicket getTicket();

	RepositoryUser getTicketOwner();

}
