package fr.openwide.alfresco.component.model.search.model.highlight;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryFieldHighlightParameters;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryGeneralHighlightParameters;
import fr.openwide.alfresco.api.core.search.model.highlight.RepositoryHighlightResults;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;

/**
 * Permet de demander à Solr de récuperer des extraits des documents où il a trouvé le mot cherché.
 * 
 * On récupère les extraits à l'aide de 
 * @see {@link RepositoryHighlightResults#extractFromNode(fr.openwide.alfresco.api.core.node.model.RepositoryNode)
 * 
 * @author asauvez
 *
 */
public class GeneralHighlightBuilder {
	
	private final SearchQueryBuilder searchQueryBuilder;
	private final List<NameReference> fields;
	
	private String prefix = "<strong>";
	private String postfix = "</strong>";

	private Integer snippetCount = null;
	private Integer fragmentSize = null;
	private Boolean mergeContiguous = null;

	public GeneralHighlightBuilder(SearchQueryBuilder searchQueryBuilder, List<NameReference> fields) {
		this.searchQueryBuilder = searchQueryBuilder;
		this.fields = fields;
	}

	public GeneralHighlightBuilder field(NameReference field) {
		this.fields.add(field);
		return this;
	}
	public GeneralHighlightBuilder field(PropertyModel<?> field) {
		return field(field.getNameReference());
	}

	public GeneralHighlightBuilder prefix(String prefix) {
		this.prefix = prefix;
		return this;
	}
	public GeneralHighlightBuilder postfix(String postfix) {
		this.postfix = postfix;
		return this;
	}
	public GeneralHighlightBuilder snippetCount(Integer snippetCount) {
		this.snippetCount = snippetCount;
		return this;
	}
	public GeneralHighlightBuilder fragmentSize(Integer fragmentSize) {
		this.fragmentSize = fragmentSize;
		return this;
	}
	public GeneralHighlightBuilder mergeContiguous(Boolean mergeContiguous) {
		this.mergeContiguous = mergeContiguous;
		return this;
	}
	
	public SearchQueryBuilder of() {
		searchQueryBuilder.getParameters().setHighlight(getHighlight());
		return searchQueryBuilder;
	}
	
	private RepositoryGeneralHighlightParameters getHighlight() {
		List<RepositoryFieldHighlightParameters> rfields = new ArrayList<>();
		for (NameReference field : fields) {
			rfields.add(new RepositoryFieldHighlightParameters(field, null, null, null, null, null));
		}
		return new RepositoryGeneralHighlightParameters(snippetCount, fragmentSize, mergeContiguous, prefix, postfix, null, null, rfields);
	}
}
