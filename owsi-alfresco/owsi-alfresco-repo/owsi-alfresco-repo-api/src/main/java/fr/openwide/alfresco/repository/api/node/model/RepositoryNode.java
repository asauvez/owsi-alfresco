package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.As;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

import fr.openwide.alfresco.repository.api.node.binding.RepositoryNodeSerializer;
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

	@JsonIgnore
	public Map<NameReference, Serializable> getProperties() {
		return properties;
	}
	@JsonProperty("properties")
	@JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT)
	@JsonSubTypes({
		@JsonSubTypes.Type(value=Date.class, name = "date"),
		@JsonSubTypes.Type(value=Date[].class, name = "dateList"),
		@JsonSubTypes.Type(value=Locale.class, name = "locale"),
		@JsonSubTypes.Type(value=Locale[].class, name = "localeList"),
		@JsonSubTypes.Type(value=Long.class, name = "long"),
		@JsonSubTypes.Type(value=Long[].class, name = "longList"),
		@JsonSubTypes.Type(value=Float.class, name = "float"),
		@JsonSubTypes.Type(value=Float[].class, name = "floatList"),
		@JsonSubTypes.Type(value=Double.class, name = "double"),
		@JsonSubTypes.Type(value=Double[].class, name = "doubleList"),
		@JsonSubTypes.Type(value=NameReference.class, name = "nameReference"),
		@JsonSubTypes.Type(value=NameReference[].class, name = "nameReferenceList"),
		@JsonSubTypes.Type(value=NodeReference.class, name = "nodeReference"),
		@JsonSubTypes.Type(value=NodeReference[].class, name = "nodeReferenceList"),
		
		@JsonSubTypes.Type(value=RepositoryContentData.class, name = "content"),
		@JsonSubTypes.Type(value=ArrayList.class, name = "list"),
	})
	private Map<NameReference, Serializable> getPropertiesJson() {
		return RepositoryNodeSerializer.toJSon(properties);
	}
	
	@SuppressWarnings("unused")
	private void setPropertiesJson(Map<NameReference, Serializable> propertiesJSon) {
		RepositoryNodeSerializer.toNative(properties, propertiesJSon);
	}
	
	@JsonIgnore
	public Map<NameReference, Serializable> getExtensions() {
		return extensions;
	}
	@JsonProperty("extensions")
	@JsonTypeInfo(use=Id.NAME, include=As.WRAPPER_OBJECT)
	@JsonSubTypes({
		@JsonSubTypes.Type(value=Date.class, name = "date"),
		@JsonSubTypes.Type(value=Date[].class, name = "dateList"),
		@JsonSubTypes.Type(value=Locale.class, name = "locale"),
		@JsonSubTypes.Type(value=Locale[].class, name = "localeList"),
		@JsonSubTypes.Type(value=Long.class, name = "long"),
		@JsonSubTypes.Type(value=Long[].class, name = "longList"),
		@JsonSubTypes.Type(value=Float.class, name = "float"),
		@JsonSubTypes.Type(value=Float[].class, name = "floatList"),
		@JsonSubTypes.Type(value=Double.class, name = "double"),
		@JsonSubTypes.Type(value=Double[].class, name = "doubleList"),
		@JsonSubTypes.Type(value=NameReference.class, name = "nameReference"),
		@JsonSubTypes.Type(value=NameReference[].class, name = "nameReferenceList"),
		@JsonSubTypes.Type(value=NodeReference.class, name = "nodeReference"),
		@JsonSubTypes.Type(value=NodeReference[].class, name = "nodeReferenceList"),
		
		@JsonSubTypes.Type(value=RepositoryContentData.class, name = "content"),
		@JsonSubTypes.Type(value=ArrayList.class, name = "list"),
	})
	private Map<NameReference, Serializable> getExtensionsJson() {
		return RepositoryNodeSerializer.toJSon(extensions);
	}
	
	@SuppressWarnings("unused")
	private void setExtensionsJson(Map<NameReference, Serializable> extensionsJSon) {
		RepositoryNodeSerializer.toNative(extensions, extensionsJSon);
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
