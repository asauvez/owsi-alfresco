package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ChildAssociationReference implements Serializable {

	private RepositoryNode parentNode;
	private NameReference type;

	public ChildAssociationReference() {}

	public ChildAssociationReference(RepositoryNode parentNode, NameReference type) {
		this.parentNode = parentNode;
		this.type = type;
	}

	public RepositoryNode getParentNode() {
		return parentNode;
	}
	public void setParentNode(RepositoryNode parentNode) {
		this.parentNode = parentNode;
	}

	public NameReference getType() {
		return type;
	}
	public void setType(NameReference type) {
		this.type = type;
	}

}
