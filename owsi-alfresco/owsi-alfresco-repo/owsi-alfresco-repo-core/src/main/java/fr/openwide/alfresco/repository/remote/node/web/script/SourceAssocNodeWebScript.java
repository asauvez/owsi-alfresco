package fr.openwide.alfresco.repository.remote.node.web.script;

import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService.SOURCE_ASSOC_NODE_SERVICE;

public class SourceAssocNodeWebScript extends AbstractNodeWebScript<List<RepositoryNode>, SOURCE_ASSOC_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> executeImpl(Resource content, SOURCE_ASSOC_NODE_SERVICE parameter,  WebScriptRequest req, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeService.getTargetAssocs(parameter.nodeReference, parameter.assocName, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(SOURCE_ASSOC_NODE_SERVICE.class);
	}

}
