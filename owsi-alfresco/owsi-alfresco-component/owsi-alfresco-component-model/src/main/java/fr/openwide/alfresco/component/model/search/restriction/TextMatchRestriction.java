package fr.openwide.alfresco.component.model.search.restriction;

import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.component.model.node.model.property.TextPropertyModel;

public class TextMatchRestriction extends MatchRestriction<String> {

	public TextMatchRestriction(RestrictionBuilder parent, TextPropertyModel property, String value) {
		super(parent, property, value);
	}

	@Override
	protected String toLuceneQueryInternal() {
		return (StringUtils.isNotBlank(value)) ? super.toLuceneQueryInternal() : "";
	}
}
