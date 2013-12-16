package fr.openwide.alfresco.query.web.form.projection.node;

import java.io.Serializable;

import com.google.common.base.Predicate;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;
import fr.openwide.alfresco.query.api.search.model.NodeResult;
import fr.openwide.alfresco.query.core.node.model.property.PropertyModel;
import fr.openwide.alfresco.query.web.form.projection.Projection;
import fr.openwide.alfresco.query.web.form.projection.ProjectionBuilder;

public class NodeProjectionBuilder extends ProjectionBuilder<NodeResult, NodeProjectionBuilder> {

	public Projection<NodeResult, NodeProjectionBuilder, NodeReference> ref() {
		return add(new NodeReferenceProjectionImpl(this));
	}

	public Projection<NodeResult, NodeProjectionBuilder, NameReference> type() {
		return add(new NodeTypeProjectionImpl(this));
	}

	public <P extends Serializable> Projection<NodeResult, NodeProjectionBuilder, P> prop(PropertyModel<P> property) {
		return add(new NodePropertyProjectionImpl<P>(this, property));
	}

	public Predicate<NodeResult> ifHasPermission(String permission) {
		return new UserPermissionPredicate(permission);
	}

}
