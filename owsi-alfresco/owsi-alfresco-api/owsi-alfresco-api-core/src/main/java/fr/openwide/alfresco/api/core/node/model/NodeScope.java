package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

import fr.openwide.alfresco.api.core.node.binding.content.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryVisitor.RepositoryVisitable;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

@JsonInclude(Include.NON_EMPTY)
public class NodeScope implements Serializable, RepositoryVisitable<NodeScope> {

	private static final long serialVersionUID = 6930653481257487738L;

	private boolean nodeReference = true;
	private boolean path = false;
	private boolean type = false;
	private final Set<NameReference> properties = new LinkedHashSet<>();
	private final Set<NameReference> aspects = new LinkedHashSet<>();

	private final Map<NameReference, NodeContentDeserializer<?>> contentDeserializers = new LinkedHashMap<>();

	private final Map<NameReference, String> extensions = new LinkedHashMap<>();

	private NodeScope primaryParent;
	private boolean recursivePrimaryParent;
	private final Map<NameReference, NodeScope> renditions = new LinkedHashMap<>();
	private final Map<NameReference, NodeScope> childAssociations = new LinkedHashMap<>();
	private final Map<NameReference, NodeScope> parentAssociations = new LinkedHashMap<>();
	private final Map<NameReference, NodeScope> targetAssocs = new LinkedHashMap<>();
	private final Map<NameReference, NodeScope> sourceAssocs = new LinkedHashMap<>();

	private Set<NameReference> recursiveChildAssociations = new LinkedHashSet<>();
	private Set<NameReference> recursiveParentAssociations = new LinkedHashSet<>();

	private Set<RepositoryPermission> userPermissions = new LinkedHashSet<>();
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
	public boolean isRecursivePrimaryParent() {
		return recursivePrimaryParent;
	}
	public void setRecursivePrimaryParent(boolean recursivePrimaryParent) {
		this.recursivePrimaryParent = recursivePrimaryParent;
	}
	public Set<NameReference> getProperties() {
		return properties;
	}
	
	@JsonProperty("contents")
	private Set<NameReference> getContentsJson() {
		return contentDeserializers.keySet();
	}
	@SuppressWarnings("unused")
	private void setContentsJson(Set<NameReference> nameReferences) {
		for (NameReference nameReference : nameReferences) {
			contentDeserializers.put(nameReference, null);
		}
	}
	@JsonIgnore
	public Map<NameReference, NodeContentDeserializer<?>> getContentDeserializers() {
		return contentDeserializers;
	}
	
	public Set<NameReference> getAspects() {
		return aspects;
	}
	
	public Map<NameReference, String> getExtensions() {
		return extensions;
	}

	public Map<NameReference, NodeScope> getRenditions() {
		return renditions;
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

	@Override
	public void visit(RepositoryVisitor<NodeScope> visitor) {
		visitor.visit(this);
		
		visitor.visitMap("renditions", renditions);
		visitor.visitMap("childAssociations", childAssociations);
		visitor.visitMap("parentAssociations", parentAssociations);
		visitor.visitMap("sourceAssocs", sourceAssocs);
		visitor.visitMap("targetAssocs", targetAssocs);
	}

}
