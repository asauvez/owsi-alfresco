package fr.openwide.alfresco.repo.dictionary.search.service.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.alfresco.repo.search.impl.parsers.FTSQueryException;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.FieldHighlightParameters;
import org.alfresco.service.cmr.search.GeneralHighlightParameters;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.QueryConsistency;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.api.core.search.model.SortDefinition;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryFieldHighlightParameters;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryGeneralHighlightParameters;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;

public class NodeSearchModelRepositoryServiceImpl implements NodeSearchModelRepositoryService {

	private final Logger LOGGER = LoggerFactory.getLogger(NodeSearchModelRepositoryServiceImpl.class);
	private final Logger LOGGER_AUDIT = LoggerFactory.getLogger(NodeSearchModelRepositoryServiceImpl.class.getName() + "_Audit");

	@Autowired private SearchService searchService;
	@Autowired private TransactionService transactionService;
	@Autowired private Environment environment;
	
	@Autowired private NodeModelRepositoryService nodeModelRepositoryService;
	
	@Override
	public SearchParameters getSearchParameters(RepositorySearchParameters rsp) {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Searching for query : {}", rsp.getQuery().replace("\n", " "));
		}

		if (rsp.getQuery() == null || rsp.getQuery().isEmpty()) {
			throw new IllegalStateException("The query should not be an empty string.");
		}

		SearchParameters sp = new SearchParameters();
		for (StoreRef storeRef : rsp.getStoreRefs()) {
			sp.addStore(storeRef);
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
		
		if (rsp.getMaxPermissionChecks() != null) {
			sp.setMaxPermissionChecks(rsp.getMaxPermissionChecks());
		}
		if (rsp.getMaxPermissionCheckTimeMillis() != null) {
			sp.setMaxPermissionCheckTimeMillis(rsp.getMaxPermissionCheckTimeMillis());
		}
		
		for (SortDefinition sd : rsp.getSorts()) {
			sp.addSort(sd.getProperty().toPrefixString(), sd.isAscending());
		}
		
		RepositoryGeneralHighlightParameters rhighlight = rsp.getHighlight();
		if (rhighlight != null) {
			List<FieldHighlightParameters> fields = new ArrayList<>();
			for (RepositoryFieldHighlightParameters field : rhighlight.getFields()) {
				fields.add(new FieldHighlightParameters(
						field.getField().toPrefixString(), 
						field.getSnippetCount(), 
						field.getFragmentSize(), 
						field.getMergeContiguous(), 
						field.getPrefix(), 
						field.getPostfix()));
			}
			sp.setHighlight(new GeneralHighlightParameters(
					rhighlight.getSnippetCount(), 
					rhighlight.getFragmentSize(), 
					rhighlight.getMergeContiguous(), 
					rhighlight.getPrefix(), 
					rhighlight.getPostfix(),
					rhighlight.getMaxAnalyzedChars(), 
					rhighlight.getUsePhraseHighlighter(), 
					fields));
		}
		
		return sp;
	}
	
	@Override
	public List<NodeRef> searchReference(RestrictionBuilder restrictionBuilder) {
		return searchReference(new SearchQueryBuilder()
				.restriction(restrictionBuilder));
	}

	@Override
	public List<NodeRef> searchReference(SearchQueryBuilder searchBuilder) {
		RepositorySearchParameters rsp = searchBuilder.getParameters();
		try {
			long before = System.currentTimeMillis();

			SearchParameters sp = getSearchParameters(rsp);
			List<NodeRef> res = new ArrayList<>();
			ResultSet resultSet = searchService.query(sp);
			try {
				res = resultSet.getNodeRefs();
			} finally {
				resultSet.close();
			}
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("Returning {} result(s)", res.size());
			}
			if (LOGGER_AUDIT.isDebugEnabled()) {
				LOGGER_AUDIT.debug("{} : {} ms", rsp.getQuery().replace("\n", " "), System.currentTimeMillis() - before);
			}
			return res;
		} catch (FTSQueryException ex) {
			throw new IllegalStateException(rsp.getQuery(), ex);
		}
	}
	
	@Override
	public int searchBatch(BatchSearchQueryBuilder searchBuilder) {
		RepositorySearchParameters rsp = searchBuilder.getParameters();
		try {
			long before = System.currentTimeMillis();
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("* " + rsp.getQuery().replace("\n", " ") + " started");
			}
			
			for (String configurationName : searchBuilder.getConfigurationName()) {
				searchBuilder
					.frameSize(environment.getProperty(configurationName + ".frameSize", Integer.class, searchBuilder.getFrameSize()))
					.transactionReadOnly(environment.getProperty(configurationName + ".transactionReadOnly", Boolean.class, searchBuilder.isTransactionReadOnly()))
					.transactionSize(environment.getProperty(configurationName + ".transactionSize", Integer.class, searchBuilder.getTransactionSize()))
					.replaceFtsQuery(environment.getProperty(configurationName + ".replaceFtsQuery"))
					.appendFtsQuery(environment.getProperty(configurationName + ".appendFtsQuery"))
					.fakeResults(environment.getProperty(configurationName + ".fakeResults"))
					;
			}
			if (searchBuilder.getAlreadyDoneAspect() != null) {
				searchBuilder.getParameters().setQuery("(" + searchBuilder.getParameters().getQuery() + ") AND "
						+ new RestrictionBuilder().hasAspect(searchBuilder.getAlreadyDoneAspect()).not().of().toFtsQuery()); 
			}
			
			SearchParameters sp = getSearchParameters(rsp);
			int nbTotal = 0;
			if (searchBuilder.getFrameSize() == null) {
				nbTotal += consumeInFrame(searchBuilder, sp);
			} else {
				sp.setLimitBy(LimitBy.FINAL_SIZE);
				// Force sort pour que tri soit prédictif
				// sp.addSort(SysModel.referenceable.nodeUuid.getQName().getFullName(), true);
				
				for (int batchNumber = 0; ; batchNumber ++) {
					int skipCount = batchNumber * searchBuilder.getFrameSize();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("** Start of frame " + skipCount + "/" + searchBuilder.getFrameSize());
					}
					sp.setSkipCount(skipCount);
					sp.setMaxItems(searchBuilder.getFrameSize());
				
					int consumedInFrame = consumeInFrame(searchBuilder, sp);
					if (consumedInFrame == 0) break;
					
					nbTotal += consumedInFrame;
				}
			}
			
			if (LOGGER_AUDIT.isDebugEnabled()) {
				LOGGER_AUDIT.debug("* {} : {} ms", rsp.getQuery().replace("\n", " "), System.currentTimeMillis() - before);
			}
			return nbTotal;
		} catch (FTSQueryException ex) {
			throw new IllegalStateException(rsp.getQuery(), ex);
		}
	}
	
	private int consumeInFrame(BatchSearchQueryBuilder searchBuilder, SearchParameters sp) {
		ResultSet resultSet = searchService.query(sp);
		int nbInFrame = 0;
		try {
			Iterator<NodeRef> iterator = (searchBuilder.getFakeResults() == null) 
					? new ResultSetRowIterator(resultSet.iterator())
					: searchBuilder.getFakeResults().iterator();
			if (searchBuilder.getTransactionSize() == null) {
				nbInFrame += consumeInTransaction(searchBuilder, iterator);
			} else {
				while (iterator.hasNext()) {
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("*** Start of transaction " + nbInFrame + "/" + resultSet.length());
					}
					nbInFrame += transactionService.getRetryingTransactionHelper().doInTransaction(new RetryingTransactionCallback<Integer>() {
						@Override
						public Integer execute() {
							return consumeInTransaction(searchBuilder, iterator);
						}
					}, searchBuilder.isTransactionReadOnly(), true);
				}
			}
		} finally {
			resultSet.close();
		}
		return nbInFrame;
	}
	
	private class ResultSetRowIterator implements Iterator<NodeRef> {
		private final Iterator<ResultSetRow> iterator;
		public ResultSetRowIterator(Iterator<ResultSetRow> iterator) {
			this.iterator = iterator;
		}
		@Override public boolean hasNext() {
			return iterator.hasNext();
		}
		@Override public NodeRef next() {
			return iterator.next().getNodeRef();
		}
	}
	
	private int consumeInTransaction(BatchSearchQueryBuilder searchBuilder, Iterator<NodeRef> iterator) {
		int nbInBatch = 0;
		while (iterator.hasNext() && (searchBuilder.getTransactionSize() == null || nbInBatch < searchBuilder.getTransactionSize())) {
			NodeRef nodeRef = iterator.next();
			if (nodeModelRepositoryService.exists(nodeRef)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("**** Consume " + nbInBatch + "/" + nodeRef);
				}
				if (searchBuilder.getConsumer() != null) {
					searchBuilder.getConsumer().accept(nodeRef);
				}
				
				if (searchBuilder.getAlreadyDoneAspect() != null) {
					nodeModelRepositoryService.addAspect(nodeRef, searchBuilder.getAlreadyDoneAspect());
				}
				
				nbInBatch ++;
			} else {
				LOGGER.warn(nodeRef + " does not exist");
			}
		}
		return nbInBatch;
	}
	
	@Override
	public Optional<NodeRef> searchReferenceUnique(RestrictionBuilder restrictionBuilder) {
		List<NodeRef> results = searchReference(restrictionBuilder);
		if (results.size() > 1) {
			throw new IllegalStateException(results.size() + " results, expected 0 or 1. Query : " + restrictionBuilder.toFtsQuery());
		} else if (results.size() == 1) {
			return Optional.of(results.get(0));
		} else {
			return Optional.empty();
		}
	}
	
	@Override
	public NodeRef searchReferenceMandatory(RestrictionBuilder restrictionBuilder) {
		List<NodeRef> results = searchReference(restrictionBuilder);
		if (results.size() != 1) {
			throw new IllegalStateException(results.size() + " results, expected 1. Query : " + restrictionBuilder.toFtsQuery());
		} else {
			return results.get(0);
		}
	}
}
