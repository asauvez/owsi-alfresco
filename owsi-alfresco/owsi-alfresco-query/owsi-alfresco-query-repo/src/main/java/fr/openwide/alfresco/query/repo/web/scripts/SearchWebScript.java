package fr.openwide.alfresco.query.repo.web.scripts;

import java.io.IOException;
import java.util.List;

import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.query.api.search.model.NodeFetchDetails;
import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.api.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.query.api.search.service.NodeSearchRemoteService.SEARCH_NODE_SERVICE;
import fr.openwide.alfresco.query.repo.exception.AbstractRemoteException;
import fr.openwide.alfresco.query.repo.exception.RemoteArgumentException;

public class SearchWebScript extends AbstractJacksonJavaBackedWebScript<List<NodeResult>> {

	private NodeSearchRemoteService nodeSearchService;

	@Override
	protected List<NodeResult> executeImpl(WebScriptRequest req, WebScriptResponse res, Status status, Cache cache) throws AbstractRemoteException {
		String query = req.getParameter(SEARCH_NODE_SERVICE.PARAMETER_QUERY);
		try {
			NodeFetchDetails nodeFetchDetails = objectMapperProvider.getMapper().readValue(req.getParameter(SEARCH_NODE_SERVICE.PARAMETER_NODE_FETCH_DETAIL), NodeFetchDetails.class);
			return nodeSearchService.search(query, nodeFetchDetails);
		} catch (IOException e) {
			throw new RemoteArgumentException(e);
		}
	}

	public void setNodeSearchService(NodeSearchRemoteService nodeSearchService) {
		this.nodeSearchService = nodeSearchService;
	}

}
