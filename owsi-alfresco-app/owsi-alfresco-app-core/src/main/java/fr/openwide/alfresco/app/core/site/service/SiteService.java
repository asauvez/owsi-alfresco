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

	void addCollaborator(SiteReference siteReference, AuthorityReference authorityReference);
	void removeCollaborator(SiteReference siteReference, AuthorityReference authorityReference);

	void addConsumer(SiteReference siteReference, AuthorityReference authorityReference);
	void removeConsumer(SiteReference siteReference, AuthorityReference authorityReference);

	void addContributor(SiteReference siteReference, AuthorityReference authorityReference);
	void removeContributor(SiteReference siteReference, AuthorityReference authorityReference);

	void addManager(SiteReference siteReference, AuthorityReference authorityReference);
	void removeManager(SiteReference siteReference, AuthorityReference authorityReference);

}
