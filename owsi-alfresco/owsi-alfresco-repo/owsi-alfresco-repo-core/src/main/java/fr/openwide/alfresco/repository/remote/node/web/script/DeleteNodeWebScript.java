package fr.openwide.alfresco.repository.remote.node.web.script;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.repository.api.remote.exception.RepositoryRemoteException;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class DeleteNodeWebScript extends AbstractNodeWebScript<Void, NodeReference> {

	@Override
	protected Void executeImpl(Resource content, NodeReference nodeReference, Status status, Cache cache) throws RepositoryRemoteException {
		nodeService.delete(nodeReference);
		return null;
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(NodeReference.class);
	}

}
