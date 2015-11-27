package fr.openwide.alfresco.app.core.node.model;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class NodeReferenceType extends AbstractStringUserType<NodeReference> {

	@Override
	public Class<NodeReference> returnedClass() {
		return NodeReference.class;
	}

	@Override
	protected NodeReference getAsObject(String value) {
		return NodeReference.create(value);
	}

}
