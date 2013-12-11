package fr.openwide.alfresco.app.core.authentication.model;

import fr.openwide.alfresco.repository.api.authentication.model.RepositoryTicket;

public interface RepositoryTicketAware {

	RepositoryTicket getTicket();

}
