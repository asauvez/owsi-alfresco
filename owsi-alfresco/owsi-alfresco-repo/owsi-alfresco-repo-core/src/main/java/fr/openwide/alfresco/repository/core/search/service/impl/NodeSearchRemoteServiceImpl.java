package fr.openwide.alfresco.repository.core.search.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.search.impl.parsers.FTSQueryException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.apache.commons.lang.StringUtils;

import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.node.model.NodeFetchDetails;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.service.NodeRemoteService;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;
import fr.openwide.alfresco.repository.api.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.repository.core.node.service.impl.NodeRemoteServiceImpl;
import fr.openwide.alfresco.repository.core.remote.service.ConversionService;
import fr.openwide.alfresco.repository.remote.framework.exception.InvalidPayloadException;

public class NodeSearchRemoteServiceImpl implements NodeSearchRemoteService {

	private NodeRemoteService nodeRemoteService;
	private SearchService searchService;
	private ConversionService conversionService;

	@Override
	public List<RepositoryNode> search(String luceneQuery, StoreReference storeReference, NodeFetchDetails details) {
		if (StringUtils.isBlank(luceneQuery)) {
			throw new InvalidPayloadException("The query should not be an empty string.");
		}
		try {
			ResultSet resultSet = searchService.query(
					conversionService.getRequired(storeReference), 
					SearchService.LANGUAGE_FTS_ALFRESCO, 
					luceneQuery);
			List<RepositoryNode> res = new ArrayList<>();
			for (NodeRef nodeRef : resultSet.getNodeRefs()) {
				try {
					res.add(nodeRemoteService.get(conversionService.get(nodeRef), details));
				} catch (NoSuchNodeException e) {
					// ignore : cela doit être des noeuds effacés, mais dont l'effacement n'est pas encore pris en compte dans la recherche.
				}
			}
			return res;
		} catch (FTSQueryException ex) {
			throw new InvalidPayloadException(luceneQuery, ex);
		}
	}

	public void setNodeRemoteService(NodeRemoteServiceImpl nodeRemoteService) {
		this.nodeRemoteService = nodeRemoteService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
