package fr.openwide.alfresco.repo.core.bootstrap.builder;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteRole;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.repo.core.bootstrap.service.impl.BootstrapServiceImpl;

public class SiteBootstrap {

	private SiteInfo siteInfo;
	private BootstrapServiceImpl bootstrapService;

	public SiteBootstrap(SiteInfo siteInfo, BootstrapServiceImpl bootstrapService) {
		this.siteInfo = siteInfo;
		this.bootstrapService = bootstrapService;
	}
	
	public SiteInfo getSiteInfo() {
		return siteInfo;
	}
	public NodeRef getNodeRef() {
		return siteInfo.getNodeRef();
	}
	public String getShortName() {
		return siteInfo.getShortName();
	}

	public SiteBootstrap membership(AuthorityReference authority, SiteRole role) {
		bootstrapService.setSiteMembership(siteInfo, authority, role);
		return this;
	}
	
	public FolderBootstrap documentLibrary() {
		return new FolderBootstrap(bootstrapService.getOrCreateDocumentLibrary(siteInfo), bootstrapService);
	}
	
	public NodeRef dataListContainer() {
		return bootstrapService.getOrCreateDataListContainer(siteInfo);
	}
}
