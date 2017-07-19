package fr.openwide.alfresco.app.core.site.service;

import java.util.Optional;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.site.model.SiteReference;
import fr.openwide.alfresco.app.core.site.model.RepositorySite;

public interface SiteService {

	public SiteReference createSite(RepositorySite site);

	Optional<NodeReference> getSiteNodeReference(SiteReference siteReference);

	public void deleteSite(SiteReference siteReference);

	void addCollaborator(SiteReference siteReference, RepositoryAuthority repositoryAuthority);
	void removeCollaborator(SiteReference siteReference, RepositoryAuthority repositoryAuthority);

	void addConsumer(SiteReference siteReference, RepositoryAuthority repositoryAuthority);
	void removeConsumer(SiteReference siteReference, RepositoryAuthority repositoryAuthority);

	void addContributor(SiteReference siteReference, RepositoryAuthority repositoryAuthority);
	void removeContributor(SiteReference siteReference, RepositoryAuthority repositoryAuthority);

	void addManager(SiteReference siteReference, RepositoryAuthority repositoryAuthority);
	void removeManager(SiteReference siteReference, RepositoryAuthority repositoryAuthority);

}
