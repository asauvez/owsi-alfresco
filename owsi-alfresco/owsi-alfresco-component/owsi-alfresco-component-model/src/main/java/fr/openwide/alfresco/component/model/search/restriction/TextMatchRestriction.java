package fr.openwide.alfresco.component.model.search.restriction;

import fr.openwide.alfresco.component.model.node.model.property.TextPropertyModel;

public class TextMatchRestriction extends MatchRestriction<String> {

	public TextMatchRestriction(RestrictionBuilder parent, TextPropertyModel property, String value) {
		super(parent, property, value);
	}

}
