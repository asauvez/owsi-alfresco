package fr.openwide.alfresco.api.core.node.model;

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

import fr.openwide.alfresco.api.core.node.binding.property.NodePropertyDeserializer;
import fr.openwide.alfresco.api.core.node.binding.property.NodePropertySerializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryVisitor.RepositoryVisitable;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

@JsonInclude(Include.NON_EMPTY)
public class RepositoryNode implements Serializable, RepositoryVisitable<RepositoryNode> {

	private static final long serialVersionUID = 6930653481257487738L;

	private NodeReference nodeReference;
	private String path;
	private NameReference type;
	private RepositoryChildAssociation primaryParentAssociation;
	
	private final Map<NameReference, Serializable> properties = new LinkedHashMap<>();
	private final Set<NameReference> aspects = new LinkedHashSet<>();

	private final Map<NameReference, Serializable> extensions = new LinkedHashMap<>();
	
	private final Map<NameReference, Object> contents = new LinkedHashMap<>();

	private final Map<NameReference, RepositoryNode> renditions = new LinkedHashMap<>();
	private final Map<NameReference, List<RepositoryNode>> childAssociations = new LinkedHashMap<>();
	private final Map<NameReference, List<RepositoryNode>> parentAssociations = new LinkedHashMap<>();
	private final Map<NameReference, List<RepositoryNode>> targetAssocs = new LinkedHashMap<>();
	private final Map<NameReference, List<RepositoryNode>> sourceAssocs = new LinkedHashMap<>();

	private final Set<RepositoryPermission> userPermissions = new HashSet<>();
	private Boolean inheritParentPermissions;
	private final Set<RepositoryAccessControl> accessControlList = new LinkedHashSet<>();

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
	@SuppressWarnings({ "unchecked", "unused" })
	public <T> T getProperty(NameReference nameReference, Class<T> clazz) {
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
	@SuppressWarnings({ "unchecked", "unused" })
	public <T> T getExtension(NameReference nameReference, Class<T> clazz) {
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

	public Map<NameReference, RepositoryNode> getRenditions() {
		return renditions;
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
	public Set<RepositoryAccessControl> getAccessControlList() {
		return accessControlList;
	}

	@Override
	public void visit(RepositoryVisitor<RepositoryNode> visitor) {
		visitor.visit(this);
		
		visitor.visitMap("renditions", renditions);
		visitor.visitMapList("childAssociations", childAssociations);
		visitor.visitMapList("parentAssociations", parentAssociations);
		visitor.visitMapList("sourceAssocs", sourceAssocs);
		visitor.visitMapList("targetAssocs", targetAssocs);
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
