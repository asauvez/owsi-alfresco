package fr.openwide.alfresco.query.repo.web.scripts;

import java.util.List;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.SimpleType;

import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.api.search.service.NodeSearchRemoteService.TARGET_ASSOC_NODE_SERVICE;
import fr.openwide.alfresco.repository.api.remote.model.RepositoryRemoteException;

public class TargetAssocNodeWebScript extends AbstractNodeSearchWebScript<List<NodeResult>, TARGET_ASSOC_NODE_SERVICE> {

	@Override
	protected List<NodeResult> executeImpl(TARGET_ASSOC_NODE_SERVICE parameter, Status status, Cache cache) throws RepositoryRemoteException {
		return nodeSearchService.getTargetAssocs(parameter.nodeReference, parameter.assocName, parameter.nodeFetchDetails);
	}

	@Override
	protected JavaType getParameterType() {
		return SimpleType.construct(TARGET_ASSOC_NODE_SERVICE.class);
	}

}
