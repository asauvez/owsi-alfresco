package fr.openwide.alfresco.repo.dictionary.search.service.impl;

import java.util.Iterator;

import org.alfresco.repo.search.impl.parsers.FTSQueryException;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.search.LimitBy;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.ResultSetRow;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.transaction.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.search.model.RepositorySearchParameters;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.impl.NodeSearchModelServiceImpl;
import fr.openwide.alfresco.repo.core.search.service.impl.NodeSearchRemoteServiceImpl;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.remote.framework.exception.InvalidPayloadException;

public class NodeSearchModelRepositoryServiceImpl 
	extends NodeSearchModelServiceImpl
	implements NodeSearchModelRepositoryService {

	private final Logger LOGGER = LoggerFactory.getLogger(NodeSearchModelRepositoryServiceImpl.class);
	private final Logger LOGGER_AUDIT = LoggerFactory.getLogger(NodeSearchModelRepositoryServiceImpl.class.getName() + "_Audit");

	@Autowired private SearchService searchService;
	@Autowired private TransactionService transactionService;
	@Autowired private Environment environment;
	
	@Autowired private NodeModelRepositoryService nodeModelRepositoryService;
	@Autowired private ConversionService conversionService;
	
	private NodeSearchRemoteServiceImpl nodeSearchRemoteService;
	
	public NodeSearchModelRepositoryServiceImpl(NodeSearchRemoteServiceImpl nodeSearchRemoteService) {
		super(nodeSearchRemoteService);
		this.nodeSearchRemoteService = nodeSearchRemoteService;
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
			
			SearchParameters sp = nodeSearchRemoteService.getSearchParameters(rsp);
			int nbTotal = 0;
			if (searchBuilder.getFrameSize() == null) {
				nbTotal += consumeInFrame(searchBuilder, sp);
			} else {
				sp.setLimitBy(LimitBy.FINAL_SIZE);
				// Force sort pour que tri soit prédictif
				sp.addSort(SysModel.referenceable.nodeUuid.getNameReference().getFullName(), true);
				
				for (int batchNumber = 0; ; batchNumber ++) {
					int skipCount = batchNumber * searchBuilder.getFrameSize();
					if (LOGGER.isDebugEnabled()) {
						LOGGER.debug("** Start of frame " + skipCount + "/" + searchBuilder.getFrameSize());
					}
					sp.setSkipCount(skipCount);
					sp.setMaxItems(searchBuilder.getFrameSize());
				
					nbTotal += consumeInFrame(searchBuilder, sp);
				}
			}
			
			if (LOGGER_AUDIT.isInfoEnabled()) {
				LOGGER_AUDIT.info("* {} : {} ms", rsp.getQuery().replace("\n", " "), System.currentTimeMillis() - before);
			}
			return nbTotal;
		} catch (FTSQueryException ex) {
			throw new InvalidPayloadException(rsp.getQuery(), ex);
		}
	}
	
	private int consumeInFrame(BatchSearchQueryBuilder searchBuilder, SearchParameters sp) {
		ResultSet resultSet = searchService.query(sp);
		int nbInFrame = 0;
		try {
			Iterator<NodeReference> iterator = (searchBuilder.getFakeResults() == null) 
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
	
	private class ResultSetRowIterator implements Iterator<NodeReference> {
		private final Iterator<ResultSetRow> iterator;
		public ResultSetRowIterator(Iterator<ResultSetRow> iterator) {
			this.iterator = iterator;
		}
		@Override public boolean hasNext() {
			return iterator.hasNext();
		}
		@Override public NodeReference next() {
			return conversionService.get(iterator.next().getNodeRef());
		}
	}
	
	private int consumeInTransaction(BatchSearchQueryBuilder searchBuilder, Iterator<NodeReference> iterator) {
		int nbInBatch = 0;
		while (iterator.hasNext() && (searchBuilder.getTransactionSize() == null || nbInBatch < searchBuilder.getTransactionSize())) {
			NodeReference nodeReference = iterator.next();
			if (nodeModelRepositoryService.exists(nodeReference)) {
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("**** Consume " + nbInBatch + "/" + nodeReference);
				}
				if (searchBuilder.getConsumer() != null) {
					searchBuilder.getConsumer().accept(nodeReference);
				}
				
				if (searchBuilder.getAlreadyDoneAspect() != null) {
					nodeModelRepositoryService.addAspect(nodeReference, searchBuilder.getAlreadyDoneAspect());
				}
				
				nbInBatch ++;
			} else {
				LOGGER.warn(nodeReference + " does not exist");
			}
		}
		return nbInBatch;
	}
}
