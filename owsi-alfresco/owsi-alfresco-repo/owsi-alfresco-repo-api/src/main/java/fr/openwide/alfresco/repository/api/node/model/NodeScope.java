package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

@JsonInclude(Include.NON_EMPTY)
public class NodeScope implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private boolean nodeReference = true;
	private boolean path = false;
	private boolean type = false;
	private Set<NameReference> properties = new HashSet<>();
	private Set<NameReference> contents = new HashSet<>();
	private Map<NameReference, RepositoryContentDeserializer<?>> contentDeserializers = new HashMap<>();
	private Set<NameReference> aspects = new HashSet<>();

	private NodeScope primaryParent;
	private Map<NameReference, NodeScope> childAssociations = new HashMap<>();
	private Map<NameReference, NodeScope> parentAssociations = new HashMap<>();
	private Map<NameReference, NodeScope> targetAssocs = new HashMap<>();
	private Map<NameReference, NodeScope> sourceAssocs = new HashMap<>();

	private Set<NameReference> recursiveChildAssociations = new HashSet<>();
	private Set<NameReference> recursiveParentAssociations = new HashSet<>();

	private Set<RepositoryPermission> userPermissions = new HashSet<>();
	private boolean accessPermissions = false;

	public boolean isNodeReference() {
		return nodeReference;
	}
	public void setNodeReference(boolean nodeReference) {
		this.nodeReference = nodeReference;
	}
	public boolean isPath() {
		return path;
	}
	public void setPath(boolean path) {
		this.path = path;
	}
	public boolean isType() {
		return type;
	}
	public void setType(boolean type) {
		this.type = type;
	}
	public NodeScope getPrimaryParent() {
		return primaryParent;
	}
	public void setPrimaryParent(NodeScope primaryParent) {
		this.primaryParent = primaryParent;
	}
	public Set<NameReference> getProperties() {
		return properties;
	}
	public Set<NameReference> getContents() {
		return contents;
	}
	@JsonIgnore
	public Map<NameReference, RepositoryContentDeserializer<?>> getContentDeserializers() {
		return contentDeserializers;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}

	public Map<NameReference, NodeScope> getChildAssociations() {
		return childAssociations;
	}
	public Map<NameReference, NodeScope> getParentAssociations() {
		return parentAssociations;
	}
	public Map<NameReference, NodeScope> getTargetAssocs() {
		return targetAssocs;
	}
	public Map<NameReference, NodeScope> getSourceAssocs() {
		return sourceAssocs;
	}
	
	public Set<NameReference> getRecursiveChildAssociations() {
		return recursiveChildAssociations;
	}
	public Set<NameReference> getRecursiveParentAssociations() {
		return recursiveParentAssociations;
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
