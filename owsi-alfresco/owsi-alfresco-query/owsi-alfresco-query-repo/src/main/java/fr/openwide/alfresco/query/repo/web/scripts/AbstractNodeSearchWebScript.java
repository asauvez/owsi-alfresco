package fr.openwide.alfresco.query.repo.web.scripts;

import fr.openwide.alfresco.query.api.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractPayloadRemoteWebScript;

public abstract class AbstractNodeSearchWebScript<R, P> extends AbstractPayloadRemoteWebScript<R, P> {

	protected NodeSearchRemoteService nodeSearchService;

	public void setNodeSearchService(NodeSearchRemoteService nodeSearchService) {
		this.nodeSearchService = nodeSearchService;
	}

}
