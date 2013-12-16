package fr.openwide.alfresco.query.api.search.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import fr.openwide.alfresco.query.api.node.model.NameReference;

public class NodeFetchDetails implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private boolean nodeReference;
	private boolean type;
	private Set<NameReference> properties = new HashSet<>();
	private Set<NameReference> aspects = new HashSet<>();

	private NodeFetchDetails primaryParent;
	// private Map<NameReference, NodeFetchDetails> childAssociations;
	// private Map<NameReference, NodeFetchDetails> targetAssocs;
	// private Map<NameReference, NodeFetchDetails> sourceAssocs;
	private Set<String> userPermissions = new HashSet<>();

	public boolean isNodeReference() {
		return nodeReference;
	}
	public void setNodeReference(boolean nodeReference) {
		this.nodeReference = nodeReference;
	}
	public boolean isType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	public NodeFetchDetails getPrimaryParent() {
		return primaryParent;
	}
	public void setPrimaryParent(NodeFetchDetails primaryParent) {
		this.primaryParent = primaryParent;
	}
	public Set<NameReference> getProperties() {
		return properties;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}
	public Set<String> getUserPermissions() {
		return userPermissions;
	}

}
