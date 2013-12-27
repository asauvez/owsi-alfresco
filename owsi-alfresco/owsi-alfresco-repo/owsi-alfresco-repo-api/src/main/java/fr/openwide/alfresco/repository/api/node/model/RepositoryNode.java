package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.core.io.Resource;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;


public class RepositoryNode implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private NodeReference nodeReference;
	private NameReference type;
	private RepositoryNode primaryParent;
	
	@JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT)
	@JsonSubTypes({
		@JsonSubTypes.Type(value=Date.class, name = "date"),
		@JsonSubTypes.Type(value=Long.class, name = "long"),
		@JsonSubTypes.Type(value=RepositoryContentData.class, name = "content")
	})
	private final Map<NameReference, Serializable> properties = new LinkedHashMap<>();
	private final Map<NameReference, String> contentStrings = new LinkedHashMap<>();
	private final Map<NameReference, Resource> contentResources = new LinkedHashMap<>();
	private final Set<NameReference> aspects = new LinkedHashSet<>();
	
	private Map<NameReference, List<RepositoryNode>> childAssociations = new HashMap<>();
	private Map<NameReference, List<RepositoryNode>> targetAssocs = new HashMap<>();
	private Map<NameReference, List<RepositoryNode>> sourceAssocs = new HashMap<>();

	private Set<RepositoryPermission> userPermissions = new HashSet<>();
	private Set<RepositoryAuthorityPermission> accessPermissions = new LinkedHashSet<>();

	public RepositoryNode() {
	}

	public RepositoryNode(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
	}

	public NodeReference getNodeReference() {
		return nodeReference;
	}
	public void setNodeReference(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
	}
	public NameReference getType() {
		return type;
	}
	public void setType(NameReference type) {
		this.type = type;
	}
	public RepositoryNode getPrimaryParent() {
		return primaryParent;
	}
	public void setPrimaryParent(RepositoryNode primaryParent) {
		this.primaryParent = primaryParent;
	}
	public Map<NameReference, Serializable> getProperties() {
		return properties;
	}
	public Map<NameReference, String> getContentStrings() {
		return contentStrings;
	}
	@JsonIgnore
	public Map<NameReference, Resource> getContentResources() {
		return contentResources;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}

	public Map<NameReference, List<RepositoryNode>> getChildAssociations() {
		return childAssociations;
	}
	public Map<NameReference, List<RepositoryNode>> getTargetAssocs() {
		return targetAssocs;
	}
	public Map<NameReference, List<RepositoryNode>> getSourceAssocs() {
		return sourceAssocs;
	}

	public Set<RepositoryPermission> getUserPermissions() {
		return userPermissions;
	}
	public Set<RepositoryAuthorityPermission> getAccessPermissions() {
		return accessPermissions;
	}
}
