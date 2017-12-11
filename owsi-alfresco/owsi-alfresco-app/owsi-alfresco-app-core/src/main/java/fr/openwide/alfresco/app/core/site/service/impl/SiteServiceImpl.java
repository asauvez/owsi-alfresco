package fr.openwide.alfresco.app.core.site.service.impl;

import java.util.List;

import java.util.Optional;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.api.core.authority.service.AuthorityRemoteService;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.SearchQueryLanguage;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.api.core.site.model.SiteReference;
import fr.openwide.alfresco.app.core.remote.service.impl.RepositoryRemoteBinding;
import fr.openwide.alfresco.app.core.site.model.CreateSiteParameters;
import fr.openwide.alfresco.app.core.site.service.SiteService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;

public class SiteServiceImpl implements SiteService {
	
	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/modules/delete-site")
	private static class DELETE_SITE extends WebScriptParam<Void> {
		@SuppressWarnings("unused")
		public String shortName;
	}
	
	private AuthorityRemoteService authorityService;
	private NodeSearchRemoteService nodeSearchService;
	private RepositoryRemoteBinding shareBinding;
	
	public SiteServiceImpl(AuthorityRemoteService authorityService, NodeSearchRemoteService nodeSearchService,
			RepositoryRemoteBinding shareBinding) {
		this.authorityService = authorityService;
		this.nodeSearchService = nodeSearchService;
		this.shareBinding = shareBinding;
	}
	
	@Override
	public SiteReference createSite(CreateSiteParameters site) {
		shareBinding.builder(site).call();
		return SiteReference.create(site.getShortName());
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
		return (list.isEmpty()) ? Optional.<NodeReference>empty() : Optional.of(list.get(0).getNodeReference());
	}
	
	@Override
	public void deleteSite(SiteReference siteReference) {
		DELETE_SITE payload = new DELETE_SITE();
		payload.shortName = siteReference.getName();
		shareBinding.builder(payload).call();
	}

	@Override
	public void addCollaborator(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.addToGroup(AuthorityReference.getName(), siteReference.getGroupeCollaborator().getGroupShortName());
	}
	@Override
	public void removeCollaborator(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.removeFromGroup(AuthorityReference.getName(), siteReference.getGroupeCollaborator().getGroupShortName());
	}
	
	@Override
	public void addConsumer(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.addToGroup(AuthorityReference.getName(), siteReference.getGroupeConsumer().getGroupShortName());
	}
	@Override
	public void removeConsumer(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.removeFromGroup(AuthorityReference.getName(), siteReference.getGroupeConsumer().getGroupShortName());
	}
	
	@Override
	public void addContributor(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.addToGroup(AuthorityReference.getName(), siteReference.getGroupeContributor().getGroupShortName());
	}
	@Override
	public void removeContributor(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.removeFromGroup(AuthorityReference.getName(), siteReference.getGroupeContributor().getGroupShortName());
	}
	
	@Override
	public void addManager(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.addToGroup(AuthorityReference.getName(), siteReference.getGroupeManager().getGroupShortName());
	}
	@Override
	public void removeManager(SiteReference siteReference, AuthorityReference AuthorityReference) {
		authorityService.removeFromGroup(AuthorityReference.getName(), siteReference.getGroupeManager().getGroupShortName());
	}
	
}
