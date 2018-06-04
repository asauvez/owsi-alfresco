package fr.openwide.alfresco.api.core.search.model.highlight;

import com.fasterxml.jackson.annotation.JsonCreator;

public abstract class RepositoryHighlightParameters {
	
	private final Integer snippetCount;
	private final Integer fragmentSize;

	private final Boolean mergeContiguous;

	private final String prefix;
	private final String postfix;

    @JsonCreator
	public RepositoryHighlightParameters(Integer snippetCount, Integer fragmentSize, Boolean mergeContiguous, String prefix,
			String postfix) {
		this.snippetCount = snippetCount;
		this.fragmentSize = fragmentSize;
		this.mergeContiguous = mergeContiguous;
		this.prefix = prefix;
		this.postfix = postfix;
	}

	public Integer getSnippetCount() {
		return snippetCount;
	}

	public Integer getFragmentSize() {
		return fragmentSize;
	}

	public Boolean getMergeContiguous() {
		return mergeContiguous;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getPostfix() {
		return postfix;
	}
}
