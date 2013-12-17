package fr.openwide.alfresco.repository.remote.node.web.script;


import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.remote.framework.web.script.AbstractInputStreamRemoteWebScript;

public abstract class AbstractNodeWebScript<R, P> extends AbstractInputStreamRemoteWebScript<R, P> {

	protected NodeRemoteService nodeService;

	public void setNodeService(NodeRemoteService nodeService) {
		this.nodeService = nodeService;
	}

}
