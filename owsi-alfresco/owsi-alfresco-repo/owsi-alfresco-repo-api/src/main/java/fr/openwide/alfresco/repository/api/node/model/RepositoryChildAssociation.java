package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class RepositoryChildAssociation implements Serializable {

	private RepositoryNode parentNode;
	private NameReference type;

	public RepositoryChildAssociation() {}

	public RepositoryChildAssociation(RepositoryNode parentNode, NameReference type) {
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
