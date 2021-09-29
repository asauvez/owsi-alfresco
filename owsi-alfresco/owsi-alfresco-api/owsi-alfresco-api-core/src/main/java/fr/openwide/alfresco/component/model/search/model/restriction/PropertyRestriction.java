package fr.openwide.alfresco.component.model.search.model.restriction;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class PropertyRestriction extends Restriction {

	public enum PropertyRestrictionMethod { ISNULL, ISNOTNULL, ISUNSET, EXISTS }
	
	private final PropertyModel<?> property;
	private PropertyRestrictionMethod method;

	public PropertyRestriction(RestrictionBuilder parent, 
			PropertyRestrictionMethod method,
			PropertyModel<?> property) {
		super(parent);
		this.method = method;
		this.property = property;
	}

	@Override
	protected String toFtsQueryInternal() {
		return method.name() + ":\"" + property.getNameReference().getFullyQualified() + "\"";
	}
	
	@Override
	protected String toCmisQueryWhereInternal() {
		switch (method) {
		case ISNULL:
			return property.getNameReference().getFullName() + " IS NULL";
		case ISNOTNULL:
			return property.getNameReference().getFullName() + " IS NOT NULL";
		case ISUNSET:
		case EXISTS:
		default:
			return super.toCmisQueryWhereInternal();
		}
	}

}
