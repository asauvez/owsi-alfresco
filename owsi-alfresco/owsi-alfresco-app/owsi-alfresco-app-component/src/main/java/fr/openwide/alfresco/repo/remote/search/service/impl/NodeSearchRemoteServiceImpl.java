package fr.openwide.alfresco.repo.remote.search.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.alfresco.repo.search.impl.parsers.FTSQueryException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryHighlightResult;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryHighlightResults;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.remote.framework.exception.InvalidPayloadException;

public class NodeSearchRemoteServiceImpl implements NodeSearchRemoteService {

	private final Logger LOGGER = LoggerFactory.getLogger(NodeSearchRemoteServiceImpl.class);
	private final Logger LOGGER_AUDIT = LoggerFactory.getLogger(NodeSearchRemoteServiceImpl.class.getName() + "_Audit");
	
	private NodeRemoteService nodeRemoteService;
	private SearchService searchService;
	private ConversionService conversionService;
	@Autowired private NodeSearchModelRepositoryService nodeSearchModelRepositoryService;
	
	private int maxPermissionChecks;
	
	@Override
	public List<RepositoryNode> search(RepositorySearchParameters rsp, NodeScope nodeScope) {
		try {
			long before = System.currentTimeMillis();

			SearchParameters sp = nodeSearchModelRepositoryService.getSearchParameters(rsp);
			List<RepositoryNode> res = new ArrayList<>();
			ResultSet resultSet = searchService.query(sp);
			try {
				Map<NodeRef, List<Pair<String, List<String>>>> highlighting = Collections.emptyMap();
				if (rsp.getHighlight() != null) {
					highlighting = resultSet.getHighlighting();
				}

				List<NodeRef> nodeRefs = resultSet.getNodeRefs();
				for (NodeRef nodeRef : nodeRefs) {
					try {
						RepositoryNode node = nodeRemoteService.get(conversionService.get(nodeRef), nodeScope);
						res.add(node);
						
						List<Pair<String, List<String>>> highlightingForNode = highlighting.get(nodeRef);
						if (highlightingForNode != null) {
							List<RepositoryHighlightResult> highlighResults = highlightingForNode.stream()
								.map(pair -> new RepositoryHighlightResult(NameReference.create(pair.getFirst()), pair.getSecond()))
								.collect(Collectors.toList());
							new RepositoryHighlightResults(highlighResults).storeInNode(node);
						}
					} catch (NoSuchNodeRemoteException e) {
						// ignore : cela doit être des noeuds effacés, mais dont l'effacement n'est pas encore pris en compte dans la recherche.
						LOGGER.warn("Node " + nodeRef + " not found.");
					}
				}
			} finally {
				resultSet.close();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Returning {} result(s)", res.size());
			}
			if (res.size() >= maxPermissionChecks) {
				LOGGER.warn("Search result may not have returned all results : " + res.size() + "/"+ maxPermissionChecks);
			}
			if (LOGGER_AUDIT.isDebugEnabled()) {
				LOGGER.debug("{} : {} ms", rsp.getQuery().replace("\n", " "), System.currentTimeMillis() - before);
			}
			return res;
		} catch (FTSQueryException ex) {
			throw new InvalidPayloadException(rsp.getQuery(), ex);
		}
	}
	
	public void setNodeRemoteService(NodeRemoteService nodeRemoteService) {
		this.nodeRemoteService = nodeRemoteService;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setMaxPermissionChecks(int maxPermissionChecks) {
		this.maxPermissionChecks = maxPermissionChecks;
	}
}
