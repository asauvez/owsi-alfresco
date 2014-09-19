package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.openwide.alfresco.repository.api.node.binding.NodePropertyDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.NodePropertySerializer;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

@JsonInclude(Include.NON_EMPTY)
public class RepositoryNode implements Serializable {

	private static final long serialVersionUID = 6930653481257487738L;

	private NodeReference nodeReference;
	private String path;
	private NameReference type;
	private RepositoryChildAssociation primaryParentAssociation;
	
	private final Map<NameReference, Serializable> properties = new LinkedHashMap<>();
	private final Set<NameReference> aspects = new LinkedHashSet<>();

	private final Map<NameReference, Serializable> extensions = new LinkedHashMap<>();
	
	private final Map<NameReference, Object> contents = new LinkedHashMap<>();

	private final Map<NameReference, List<RepositoryNode>> childAssociations = new LinkedHashMap<>();
	private final Map<NameReference, List<RepositoryNode>> parentAssociations = new LinkedHashMap<>();
	private final Map<NameReference, List<RepositoryNode>> targetAssocs = new LinkedHashMap<>();
	private final Map<NameReference, List<RepositoryNode>> sourceAssocs = new LinkedHashMap<>();

	private final Set<RepositoryPermission> userPermissions = new HashSet<>();
	private Boolean inheritParentPermissions;
	private final Set<RepositoryAuthorityPermission> accessPermissions = new LinkedHashSet<>();

	public RepositoryNode() {
	}

	public RepositoryNode(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
	}

	@JsonSerialize(contentUsing=NodePropertySerializer.class)
	@JsonDeserialize(contentUsing=NodePropertyDeserializer.class)
	public Map<NameReference, Serializable> getProperties() {
		return properties;
	}
	
	public Serializable getProperty(NameReference nameReference) {
		return getProperties().get(nameReference);
	}
	@SuppressWarnings("unchecked")
	public <T> T getProperty(NameReference nameReference, @SuppressWarnings("unused") Class<T> clazz) {
		return (T) getProperty(nameReference);
	}
	
	@JsonSerialize(contentUsing=NodePropertySerializer.class)
	@JsonDeserialize(contentUsing=NodePropertyDeserializer.class)
	public Map<NameReference, Serializable> getExtensions() {
		return extensions;
	}
	public Serializable getExtension(NameReference nameReference) {
		return getExtensions().get(nameReference);
	}
	@SuppressWarnings("unchecked")
	public <T> T getExtension(NameReference nameReference, @SuppressWarnings("unused") Class<T> clazz) {
		return (T) getExtension(nameReference);
	}

	public NodeReference getNodeReference() {
		return nodeReference;
	}
	public void setNodeReference(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public NameReference getType() {
		return type;
	}
	public void setType(NameReference type) {
		this.type = type;
	}

	public RepositoryChildAssociation getPrimaryParentAssociation() {
		return primaryParentAssociation;
	}
	public void setPrimaryParentAssociation(RepositoryChildAssociation primaryParentAssociation) {
		this.primaryParentAssociation = primaryParentAssociation;
	}

	@JsonIgnore
	public Map<NameReference, Object> getContents() {
		return contents;
	}
	public Set<NameReference> getAspects() {
		return aspects;
	}

	public Map<NameReference, List<RepositoryNode>> getChildAssociations() {
		return childAssociations;
	}
	public Map<NameReference, List<RepositoryNode>> getParentAssociations() {
		return parentAssociations;
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
	public Boolean getInheritParentPermissions() {
		return inheritParentPermissions;
	}
	public void setInheritParentPermissions(Boolean inheritParentPermissions) {
		this.inheritParentPermissions = inheritParentPermissions;
	}
	public Set<RepositoryAuthorityPermission> getAccessPermissions() {
		return accessPermissions;
	}

	public void visit(RepositoryNodeVisitor visitor) {
		visitor.visit(this);
		visitMap(visitor, childAssociations);
		visitMap(visitor, parentAssociations);
		visitMap(visitor, sourceAssocs);
		visitMap(visitor, targetAssocs);
	}
	private void visitMap(RepositoryNodeVisitor visitor, Map<NameReference, List<RepositoryNode>> map) {
		for (List<RepositoryNode> list : map.values()) {
			for (RepositoryNode node : list) {
				node.visit(visitor);
			}
		}
	}
	
	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof RepositoryNode) {
			RepositoryNode other = (RepositoryNode) object;
			return Objects.equals(nodeReference, other.getNodeReference());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(nodeReference);
	}

}
