package fr.openwide.alfresco.repository.core.search.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.search.impl.parsers.FTSQueryException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.model.StoreReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.SortDefinition;
import fr.openwide.alfresco.api.core.search.service.NodeSearchRemoteService;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repository.remote.framework.exception.InvalidPayloadException;

public class NodeSearchRemoteServiceImpl implements NodeSearchRemoteService {

	private final Logger LOGGER = LoggerFactory.getLogger(NodeSearchRemoteServiceImpl.class);
	private final Logger LOGGER_AUDIT = LoggerFactory.getLogger(NodeSearchRemoteServiceImpl.class.getName() + "_Audit");
	
	private NodeRemoteService nodeRemoteService;
	private SearchService searchService;
	private ConversionService conversionService;

	@Override
	public List<RepositoryNode> search(RepositorySearchParameters rsp) {
		if (rsp.getQuery() == null || rsp.getQuery().isEmpty()) {
			throw new InvalidPayloadException("The query should not be an empty string.");
		}
		try {
			long before = System.currentTimeMillis();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Searching for query : {}", rsp.getQuery().replace("\n", " "));
			}

			SearchParameters sp = new SearchParameters();
			for (StoreReference storeReference : rsp.getStoreReferences()) {
				sp.addStore(conversionService.getRequired(storeReference));
			}
			sp.setLanguage(rsp.getLanguage().getAlfrescoName());
			sp.setQuery(rsp.getQuery());
			sp.excludeDataInTheCurrentTransaction(true);
			sp.setQueryConsistency(QueryConsistency.valueOf(rsp.getQueryConsistency().name()));
			
			if (rsp.getFirstResult() != null) {
				sp.setSkipCount(rsp.getFirstResult());
				sp.setLimitBy(LimitBy.FINAL_SIZE);
			}
			if (rsp.getMaxResults() != null) {
				sp.setMaxItems(rsp.getMaxResults());
				sp.setLimitBy(LimitBy.FINAL_SIZE);
			}
			
			for (SortDefinition sd : rsp.getSorts()) {
				sp.addSort(sd.getProperty().getFullName(), sd.isAscending());
			}
			
			List<RepositoryNode> res = new ArrayList<>();
			ResultSet resultSet = searchService.query(sp);
			try {
				for (NodeRef nodeRef : resultSet.getNodeRefs()) {
					try {
						res.add(nodeRemoteService.get(conversionService.get(nodeRef), rsp.getNodeScope()));
					} catch (NoSuchNodeRemoteException e) {
						// ignore : cela doit être des noeuds effacés, mais dont l'effacement n'est pas encore pris en compte dans la recherche.
					}
				}
			} finally {
				resultSet.close();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Returning {} result(s)", res.size());
			}
			if (LOGGER_AUDIT.isInfoEnabled()) {
				LOGGER.info("{} : {} ms", rsp.getQuery().replace("\n", " "), System.currentTimeMillis() - before);
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

}
