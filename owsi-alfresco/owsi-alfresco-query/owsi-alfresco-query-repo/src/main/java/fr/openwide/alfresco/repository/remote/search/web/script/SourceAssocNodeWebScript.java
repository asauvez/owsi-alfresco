package fr.openwide.alfresco.repository.remote.search.web.script;

import java.util.List;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService.SOURCE_ASSOC_NODE_SERVICE;

public class SourceAssocNodeWebScript extends AbstractNodeSearchWebScript<List<RepositoryNode>, SOURCE_ASSOC_NODE_SERVICE> {

	@Override
	protected List<RepositoryNode> executeImpl(SOURCE_ASSOC_NODE_SERVICE parameter, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeSearchService.getTargetAssocs(parameter.nodeReference, parameter.assocName, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(SOURCE_ASSOC_NODE_SERVICE.class);
	}

}
