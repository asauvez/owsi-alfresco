package fr.openwide.alfresco.app.core.site.service;

import java.util.Optional;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.site.model.SiteReference;
import fr.openwide.alfresco.app.core.site.model.CreateSiteParameters;

public interface SiteService {

	public SiteReference createSite(CreateSiteParameters site);

	Optional<NodeReference> getSiteNodeReference(SiteReference siteReference);

	public void deleteSite(SiteReference siteReference);

	void addCollaborator(SiteReference siteReference, AuthorityReference AuthorityReference);
	void removeCollaborator(SiteReference siteReference, AuthorityReference AuthorityReference);

	void addConsumer(SiteReference siteReference, AuthorityReference AuthorityReference);
	void removeConsumer(SiteReference siteReference, AuthorityReference AuthorityReference);

	void addContributor(SiteReference siteReference, AuthorityReference AuthorityReference);
	void removeContributor(SiteReference siteReference, AuthorityReference AuthorityReference);

	void addManager(SiteReference siteReference, AuthorityReference AuthorityReference);
	void removeManager(SiteReference siteReference, AuthorityReference AuthorityReference);

}
