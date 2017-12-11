package fr.openwide.alfresco.repo.dictionary.search.model;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;

public class BatchSearchQueryBuilder extends SearchQueryBuilder {

	private final Logger LOGGER = LoggerFactory.getLogger(BatchSearchQueryBuilder.class);
	
	private Consumer<NodeReference> consumer = null;
	private String[] configurationName = new String[0];
	
	private AspectModel alreadyDoneAspect = null;
	
	private Integer frameSize = null;
	private Integer transactionSize = null;
	private boolean transactionReadOnly = false;
	
	private List<NodeReference> fakeResults = null;
	
	public BatchSearchQueryBuilder consumer(Consumer<NodeReference> consumer) {
		this.consumer = consumer;
		return this;
	}
	public Consumer<NodeReference> getConsumer() {
		return consumer;
	}
	
	public BatchSearchQueryBuilder configurationName(String ... configurationName) {
		this.configurationName = configurationName;
		return this;
	}
	public String[] getConfigurationName() {
		return configurationName;
	}
	
	public BatchSearchQueryBuilder alreadyDoneAspect(AspectModel alreadyDoneAspect) {
		this.alreadyDoneAspect = alreadyDoneAspect;
		return this;
	}
	public AspectModel getAlreadyDoneAspect() {
		return alreadyDoneAspect;
	}
	
	public BatchSearchQueryBuilder frameSize(Integer frameSize) {
		this.frameSize = frameSize;
		return this;
	}
	public Integer getFrameSize() {
		return frameSize;
	}
	public BatchSearchQueryBuilder transactionSize(Integer transactionSize) {
		this.transactionSize = transactionSize;
		return this;
	}
	public Integer getTransactionSize() {
		return transactionSize;
	}
	public BatchSearchQueryBuilder transactionReadOnly(boolean transactionReadOnly) {
		this.transactionReadOnly = transactionReadOnly;
		return this;
	}
	public boolean isTransactionReadOnly() {
		return transactionReadOnly;
	}
	
	public BatchSearchQueryBuilder replaceFtsQuery(String query) {
		if (query != null) {
			LOGGER.warn("Query replaced by " + query);
			getParameters().setQuery(query);
		}
		return this;
	}

	public BatchSearchQueryBuilder appendFtsQuery(String query) {
		if (query != null) {
			getParameters().setQuery("(" + query + ") AND (" + query + ")");
		}
		return this;
	}
	
	public BatchSearchQueryBuilder fakeResults(List<NodeReference> fakeResults) {
		this.fakeResults = fakeResults;
		return this;
	}
	public BatchSearchQueryBuilder fakeResults(String fakeResults) {
		if (fakeResults != null) {
			List<NodeReference> list = new ArrayList<>();
			for (String nodeReference : fakeResults.split(",")) {
				if (! nodeReference.trim().isEmpty()) {
					list.add(NodeReference.create(nodeReference.trim()));
				}
			}
			fakeResults(list);
		}
		return this;
	}
	public List<NodeReference> getFakeResults() {
		return fakeResults;
	}
}
