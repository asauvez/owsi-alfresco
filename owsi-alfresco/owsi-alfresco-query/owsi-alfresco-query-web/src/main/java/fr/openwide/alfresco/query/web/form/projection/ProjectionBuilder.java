package fr.openwide.alfresco.query.web.form.projection;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.web.form.projection.node.NodePropertyProjection;
import fr.openwide.alfresco.query.web.form.projection.node.NodeReferenceProjection;
import fr.openwide.alfresco.query.web.form.projection.node.NodeTypeProjection;

public class ProjectionBuilder {

	private final List<Projection<?>> projections = new ArrayList<Projection<?>>();

	public NodeReferenceProjection ref() {
		return add(new NodeReferenceProjection(this));
	}

	public NodeTypeProjection type() {
		return add(new NodeTypeProjection(this));
	}

	public <T> NodePropertyProjection<T> prop(PropertyModel<T> property) {
		return add(new NodePropertyProjection<T>(this, property));
	}

	protected <P extends Projection<?>> P add(P projection) {
		projections.add(projection);
		return projection;
	}

	public List<Projection<?>> getProjections() {
		return projections;
	}

}
