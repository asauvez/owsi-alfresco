package fr.openwide.alfresco.query.web.form.projection.node;

import fr.openwide.alfresco.query.core.node.model.value.NodeReference;
import fr.openwide.alfresco.query.core.search.model.NodeResult;

public class NodeReferenceProjection extends NodeProjection<NodeReference> {

	public NodeReferenceProjection(NodeProjectionBuilder builder) {
		super(builder, NodeReference.class);
	}
	
	@Override
	public NodeReference apply(NodeResult result) {
		return result.getReference();
	}

	@Override
	public String getDefaultLabelCode() {
		return "refprojection.label";
	}
}
