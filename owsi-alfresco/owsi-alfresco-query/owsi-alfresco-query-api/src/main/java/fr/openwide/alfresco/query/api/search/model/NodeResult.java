package fr.openwide.alfresco.query.api.search.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.openwide.alfresco.query.api.node.model.NameReference;
import fr.openwide.alfresco.query.api.node.model.NodeReference;

public class NodeResult implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private NodeReference nodeReference;
	private NameReference type;
	private NodeResult primaryParent;
	
	@JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT)
	@JsonSubTypes({
		@JsonSubTypes.Type(value=Date.class, name = "date"),
		@JsonSubTypes.Type(value=Long.class, name = "long")
	})
	private final Map<NameReference, Serializable> properties = new LinkedHashMap<>();
	private final Set<NameReference> aspects = new LinkedHashSet<>();
	private Set<String> userPermissions = new HashSet<>();

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
	public NodeResult getPrimaryParent() {
		return primaryParent;
	}
	public void setPrimaryParent(NodeResult primaryParent) {
		this.primaryParent = primaryParent;
	}
	public Map<NameReference, Serializable> getProperties() {
		return properties;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}
	public Set<String> getUserPermissions() {
		return userPermissions;
	}

}
