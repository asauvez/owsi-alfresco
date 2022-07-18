package fr.openwide.alfresco.api.core.search.model.highlight;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class RepositoryGeneralHighlightParameters extends RepositoryHighlightParameters {
	
	private final Integer maxAnalyzedChars;
	private final Boolean usePhraseHighlighter;

	private final List<RepositoryFieldHighlightParameters> fields;

	@JsonCreator
	public RepositoryGeneralHighlightParameters(@JsonProperty("snippetCount") Integer snippetCount,
			@JsonProperty("fragmentSize") Integer fragmentSize,
			@JsonProperty("mergeContiguous") Boolean mergeContiguous, @JsonProperty("prefix") String prefix,
			@JsonProperty("postfix") String postfix, @JsonProperty("maxAnalyzedChars") Integer maxAnalyzedChars,
			@JsonProperty("usePhraseHighlighter") Boolean usePhraseHighlighter,
			@JsonProperty("fields") List<RepositoryFieldHighlightParameters> fields) {
		super(snippetCount, fragmentSize, mergeContiguous, prefix, postfix);
		this.maxAnalyzedChars = maxAnalyzedChars;
		this.usePhraseHighlighter = usePhraseHighlighter;
		this.fields = fields;
	}

	public Integer getMaxAnalyzedChars() {
		return maxAnalyzedChars;
	}

	public Boolean getUsePhraseHighlighter() {
		return usePhraseHighlighter;
	}

	public List<RepositoryFieldHighlightParameters> getFields() {
		return fields;
	}

}
