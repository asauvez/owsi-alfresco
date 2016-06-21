package fr.openwide.alfresco.app.core.site.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.endpoint.PostMethodEndpoint;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;
import fr.openwide.alfresco.app.core.authority.service.AuthorityService;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.search.service.NodeSearchService;
import fr.openwide.alfresco.app.core.site.model.RepositorySite;
import fr.openwide.alfresco.app.core.site.model.SiteReference;
import fr.openwide.alfresco.app.core.site.service.SiteService;

public class SiteServiceImpl implements SiteService {
	
	private static PostMethodEndpoint<String> CREATE_SITE_ENDPOINT = new PostMethodEndpoint<String>("/modules/create-site") {};
	
	private static class DELETE_SITE {
		public static PostMethodEndpoint<String> ENDPOINT = new PostMethodEndpoint<String>("/modules/delete-site") {};
		@SuppressWarnings("unused")
		public String shortName;
	}
	
	@Autowired
	private AuthorityService authorityService;
	
	@Autowired
	private NodeSearchService nodeSearchService;
	
	
	@Autowired
	@Qualifier("shareRemoteBinding")
	private RepositoryRemoteBinding shareBinding;
	
	@Override
	public SiteReference createSite(RepositorySite site) {
		shareBinding.builder(CREATE_SITE_ENDPOINT, site).call();
		return new SiteReference(site.getShortName());
	}
	
	@Override
	public Optional<NodeReference> getSiteNodeReference(SiteReference siteReference) {
		RepositorySearchParameters searchParameters = new RepositorySearchParameters();
		searchParameters.setLanguage(SearchQueryLanguage.FTS_ALFRESCO);
		String query = "TYPE:st\\:site AND =cm\\:name:\"" + siteReference.getName() + "\"";
		searchParameters.setQuery(query);
		searchParameters.getNodeScope().setNodeReference(true);
		List<RepositoryNode> list = nodeSearchService.search(searchParameters);
		if (list.size() > 1) {
			throw new IllegalStateException(query);
		}
		return (list.isEmpty()) ? Optional.<NodeReference>absent() : Optional.of(list.get(0).getNodeReference());
	}
	
	@Override
	public void deleteSite(SiteReference siteReference) {
		DELETE_SITE payload = new DELETE_SITE();
		payload.shortName = siteReference.getName();
		shareBinding.builder(DELETE_SITE.ENDPOINT, payload).call();
	}

	@Override
	public void addCollaborator(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.addToGroup(repositoryAuthority.getName(), siteReference.getGroupeCollaborator().getGroupShortName());
	}
	@Override
	public void removeCollaborator(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.removeFromGroup(repositoryAuthority.getName(), siteReference.getGroupeCollaborator().getGroupShortName());
	}
	
	@Override
	public void addConsumer(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.addToGroup(repositoryAuthority.getName(), siteReference.getGroupeConsumer().getGroupShortName());
	}
	@Override
	public void removeConsumer(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.removeFromGroup(repositoryAuthority.getName(), siteReference.getGroupeConsumer().getGroupShortName());
	}
	
	@Override
	public void addContributor(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.addToGroup(repositoryAuthority.getName(), siteReference.getGroupeContributor().getGroupShortName());
	}
	@Override
	public void removeContributor(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.removeFromGroup(repositoryAuthority.getName(), siteReference.getGroupeContributor().getGroupShortName());
	}
	
	@Override
	public void addManager(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.addToGroup(repositoryAuthority.getName(), siteReference.getGroupeManager().getGroupShortName());
	}
	@Override
	public void removeManager(SiteReference siteReference, RepositoryAuthority repositoryAuthority) {
		authorityService.removeFromGroup(repositoryAuthority.getName(), siteReference.getGroupeManager().getGroupShortName());
	}
	
}
