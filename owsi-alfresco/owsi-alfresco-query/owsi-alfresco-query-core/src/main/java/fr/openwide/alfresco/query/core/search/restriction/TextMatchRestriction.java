package fr.openwide.alfresco.query.core.search.restriction;

import fr.openwide.alfresco.query.core.node.model.property.TextPropertyModel;

public class TextMatchRestriction extends MatchRestriction<String> {

	public TextMatchRestriction(RestrictionBuilder parent, TextPropertyModel property, String value) {
		super(parent, property, value);
	}

}
