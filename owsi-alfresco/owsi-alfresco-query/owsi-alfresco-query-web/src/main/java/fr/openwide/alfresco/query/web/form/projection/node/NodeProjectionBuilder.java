package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.core.node.model.TypeModel;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeResult;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;

public class NodeProjectionBuilder extends ProjectionBuilder<NodeResult, NodeProjectionBuilder> {

	public Projection<NodeResult, NodeProjectionBuilder, NodeReference> ref() {
		return add(new NodeReferenceProjection(this));
	}

	public Projection<NodeResult, NodeProjectionBuilder, TypeModel> type() {
		return add(new NodeTypeProjection(this));
	}

	public <T> Projection<NodeResult, NodeProjectionBuilder, T> prop(PropertyModel<T> property) {
		return add(new NodePropertyProjection<T>(this, property));
	}

}
