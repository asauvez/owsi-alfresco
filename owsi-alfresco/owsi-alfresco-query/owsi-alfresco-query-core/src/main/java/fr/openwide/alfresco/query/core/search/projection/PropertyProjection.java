package fr.openwide.alfresco.query.core.search.projection;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.core.node.model.value.NameReference;
import fr.openwide.alfresco.query.core.search.model.NodeResult;

public class PropertyProjection<P> extends Projection<P> {

	private final PropertyModel<?> property;

	public PropertyProjection(ProjectionBuilder builder, PropertyModel<P> property) {
		super(builder);
		this.property = property;
	}

	@Override
	protected String getDefaultMessage() {
		NameReference nameReference = property.getNameReference();
		return nameReference.getNamespace() + "_" + nameReference.getName();
	}

	@Override
	@SuppressWarnings("unchecked")
	public P getValue(NodeResult result) {
		return (P) result.get(property);
	}

	@Override
	public Projection.Align getAlign() {
		if (Number.class.isAssignableFrom(property.getValueClass())) {
			return Align.RIGHT;
		} else if (Boolean.class.isAssignableFrom(property.getValueClass())) {
			return Align.CENTER;
		}
		return Align.LEFT;
	}

}
