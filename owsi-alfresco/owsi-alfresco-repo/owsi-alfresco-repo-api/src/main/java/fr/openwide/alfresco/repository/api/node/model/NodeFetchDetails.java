package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;


public class NodeFetchDetails implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private boolean nodeReference;
	private boolean type;
	private Set<NameReference> properties = new HashSet<>();
	private Set<NameReference> contentsString = new HashSet<>();
	private Set<NameReference> aspects = new HashSet<>();

	private NodeFetchDetails primaryParent;
	private Map<NameReference, NodeFetchDetails> childAssociations = new HashMap<>();
	private Map<NameReference, NodeFetchDetails> targetAssocs = new HashMap<>();
	private Map<NameReference, NodeFetchDetails> sourceAssocs = new HashMap<>();
	
	private Set<RepositoryPermission> userPermissions = new HashSet<>();

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
	public Set<NameReference> getContentsString() {
		return contentsString;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}

	public Map<NameReference, NodeFetchDetails> getChildAssociations() {
		return childAssociations;
	}
	public Map<NameReference, NodeFetchDetails> getTargetAssocs() {
		return targetAssocs;
	}
	public Map<NameReference, NodeFetchDetails> getSourceAssocs() {
		return sourceAssocs;
	}

	public Set<RepositoryPermission> getUserPermissions() {
		return userPermissions;
	}

}
