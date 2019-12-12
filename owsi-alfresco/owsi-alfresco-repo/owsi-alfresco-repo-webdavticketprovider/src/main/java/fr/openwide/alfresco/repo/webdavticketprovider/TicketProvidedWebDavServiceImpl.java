package fr.openwide.alfresco.repo.webdavticketprovider;

import org.alfresco.repo.webdav.WebDavServiceImpl;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Permet de fournir une authentification automatique lors de l'édition en ligne dans MS Office.
 * On rajoute le ticket de connexion en paramètre de l'URL WebDav.
 * MS Office charge cette URL sans se poser de question.
 * 
 * Il faut exclure le chemin /alfresco/aos/ de l'authentification externe.
 * 
 * Peut être désactiver avec owsi.webdav.provideTicket=false
 * 
 * Le ticket est valable pendant une heure par défaut :
 * authentication.ticket.validDuration=PT1H
 * 
 * 
 * Ne change rien pour la partie dossier partagé WebDav !
 * 
 * @author asauvez
 */
public class TicketProvidedWebDavServiceImpl extends WebDavServiceImpl {
	
	@Autowired
	private AuthenticationService authenticationService;
	
	private boolean provideTicket = true;
	
	@Override
	public String getWebdavUrl(NodeRef nodeRef) {
		String url = super.getWebdavUrl(nodeRef);
		
		if (provideTicket) {
			url += "?ticket=" + authenticationService.getCurrentTicket();
		}
		
		return url;
	}
	
	public void setProvideTicket(boolean provideTicket) {
		this.provideTicket = provideTicket;
	}
}
