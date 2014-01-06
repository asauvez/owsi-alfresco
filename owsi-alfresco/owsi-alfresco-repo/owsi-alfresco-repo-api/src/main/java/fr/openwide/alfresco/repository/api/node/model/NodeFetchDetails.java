package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

@JsonInclude(Include.NON_EMPTY)
public class NodeFetchDetails implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private boolean nodeReference = true;
	private boolean type = false;
	private Set<NameReference> properties = new HashSet<>();
	private Set<NameReference> contentStrings = new HashSet<>();
	private Set<NameReference> aspects = new HashSet<>();

	private NodeFetchDetails primaryParent;
	private Map<NameReference, NodeFetchDetails> childAssociations = new HashMap<>();
	private Map<NameReference, NodeFetchDetails> parentAssociations = new HashMap<>();
	private Map<NameReference, NodeFetchDetails> targetAssocs = new HashMap<>();
	private Map<NameReference, NodeFetchDetails> sourceAssocs = new HashMap<>();

	private Set<RepositoryPermission> userPermissions = new HashSet<>();
	private boolean accessPermissions = false;

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
	public Set<NameReference> getContentStrings() {
		return contentStrings;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}

	public Map<NameReference, NodeFetchDetails> getChildAssociations() {
		return childAssociations;
	}
	public Map<NameReference, NodeFetchDetails> getParentAssociations() {
		return parentAssociations;
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
	public boolean isAccessPermissions() {
		return accessPermissions;
	}
	public void setAccessPermissions(boolean accessPermissions) {
		this.accessPermissions = accessPermissions;
	}

}
