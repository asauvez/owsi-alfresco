package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
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
import fr.openwide.alfresco.api.core.node.util.RepositoryNodeUtil;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

@JsonInclude(Include.NON_EMPTY)
public class RepositoryNode implements Serializable, RepositoryVisitable<RepositoryNode> {

	private NodeReference nodeReference;
	private String path;
	private NameReference type;
	private ChildAssociationReference primaryParentAssociation;
	
	private Map<NameReference, Serializable> properties;
	@JsonDeserialize(as=LinkedHashSet.class) private Set<NameReference> aspects;

	private Map<NameReference, Serializable> extensions;
	
	@JsonDeserialize(as=LinkedHashMap.class) private Map<NameReference, Object> contents;

	@JsonDeserialize(as=LinkedHashMap.class) private Map<NameReference, RepositoryNode> renditions;
	@JsonDeserialize(as=LinkedHashMap.class) private Map<NameReference, List<RepositoryNode>> childAssociations;
	@JsonDeserialize(as=LinkedHashMap.class) private Map<NameReference, List<RepositoryNode>> parentAssociations;
	@JsonDeserialize(as=LinkedHashMap.class) private Map<NameReference, List<RepositoryNode>> targetAssocs;
	@JsonDeserialize(as=LinkedHashMap.class) private Map<NameReference, List<RepositoryNode>> sourceAssocs;

	@JsonDeserialize(as=LinkedHashSet.class) private Set<PermissionReference> userPermissions;
	private Boolean inheritParentPermissions;
	private Set<RepositoryAccessControl> accessControlList;

	public RepositoryNode() {
	}

	public RepositoryNode(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
	}

	@JsonSerialize(contentUsing=NodePropertySerializer.class)
	@JsonDeserialize(contentUsing=NodePropertyDeserializer.class)
	public Map<NameReference, Serializable> getProperties() {
		return properties = RepositoryNodeUtil.init(properties);
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
		return extensions = RepositoryNodeUtil.init(extensions);
	}
	public Serializable getExtension(NameReference nameReference) {
		return getExtensions().get(nameReference);
	}
	@SuppressWarnings({ "unchecked", "unused" })
	public <T> T getExtension(NameReference nameReference, Class<T> clazz) {
		return (T) getExtension(nameReference);
	}
	public void setExtension(NameReference nameReference, Serializable value) {
		extensions = RepositoryNodeUtil.set(extensions, nameReference, value);
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

	public ChildAssociationReference getPrimaryParentAssociation() {
		return primaryParentAssociation;
	}
	public void setPrimaryParentAssociation(ChildAssociationReference primaryParentAssociation) {
		this.primaryParentAssociation = primaryParentAssociation;
	}

	@JsonIgnore
	public Map<NameReference, Object> getContents() {
		return contents = RepositoryNodeUtil.init(contents);
	}
	public void setContent(NameReference propertyName, Object content) {
		contents = RepositoryNodeUtil.set(contents, propertyName, content);
	}
	public Set<NameReference> getAspects() {
		return aspects = RepositoryNodeUtil.init(aspects);
	}

	public Map<NameReference, RepositoryNode> getRenditions() {
		return renditions = RepositoryNodeUtil.init(renditions);
	}
	
	public Map<NameReference, List<RepositoryNode>> getChildAssociations() {
		return childAssociations = RepositoryNodeUtil.init(childAssociations);
	}
	public Map<NameReference, List<RepositoryNode>> getParentAssociations() {
		return parentAssociations = RepositoryNodeUtil.init(parentAssociations);
	}
	public Map<NameReference, List<RepositoryNode>> getTargetAssocs() {
		return targetAssocs = RepositoryNodeUtil.init(targetAssocs);
	}
	public Map<NameReference, List<RepositoryNode>> getSourceAssocs() {
		return sourceAssocs = RepositoryNodeUtil.init(sourceAssocs);
	}

	public Set<PermissionReference> getUserPermissions() {
		return userPermissions = RepositoryNodeUtil.init(userPermissions);
	}
	public Boolean getInheritParentPermissions() {
		return inheritParentPermissions;
	}
	public void setInheritParentPermissions(Boolean inheritParentPermissions) {
		this.inheritParentPermissions = inheritParentPermissions;
	}
	public Set<RepositoryAccessControl> getAccessControlList() {
		return accessControlList = RepositoryNodeUtil.init(accessControlList);
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
