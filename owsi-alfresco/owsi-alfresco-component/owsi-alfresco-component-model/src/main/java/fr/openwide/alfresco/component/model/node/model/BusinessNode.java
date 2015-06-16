package fr.openwide.alfresco.component.model.node.model;

import java.io.Serializable;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.embed.AssociationsNode;
import fr.openwide.alfresco.component.model.node.model.embed.ContentsNode;
import fr.openwide.alfresco.component.model.node.model.embed.PermissionsNode;
import fr.openwide.alfresco.component.model.node.model.embed.PropertiesNode;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public class BusinessNode {

	private RepositoryNode node;
	private PropertiesNode propertiesNode = new PropertiesNode(this);
	private AssociationsNode associationsNode = new AssociationsNode(this);
	private PermissionsNode permissionsNode = new PermissionsNode(this);
	private ContentsNode contentsNode = new ContentsNode(this);
	
	public BusinessNode() {
		this(new RepositoryNode());
	}

	public BusinessNode(RepositoryNode node) {
		this.node = node;
	}

	/** Constructeur pour faciliter la modification de node. */
	public BusinessNode(NodeReference nodeReference) {
		this();
		nodeReference(nodeReference);
	}

	/** Constructeur pour faciliter la création de node. */
	public BusinessNode(NodeReference parentRef, TypeModel type, String name) {
		this();
		assocs().primaryParentRef(parentRef);
		type(type);
		properties().name(name);
	}

	public RepositoryNode getRepositoryNode() {
		return node;
	}

	public NodeReference getNodeReference() {
		return node.getNodeReference();
	}
	public BusinessNode nodeReference(NodeReference nodeReference) {
		node.setNodeReference(nodeReference);
		return this;
	}
	
	public String getPath() {
		return node.getPath();
	}
	
	public boolean isType(TypeModel typeModel) {
		return typeModel.getNameReference().equals(node.getType());
	}
	public BusinessNode type(TypeModel typeModel) {
		node.setType(typeModel.getNameReference());
		return this;
	}

	public boolean hasAspect(AspectModel aspectModel) {
		return node.getAspects().contains(aspectModel.getNameReference());
	}
	public BusinessNode aspect(AspectModel aspectModel) {
		node.getAspects().add(aspectModel.getNameReference());
		return this;
	}

	@Deprecated
	public String getName() {
		return properties().getName();
	}
	@Deprecated
	public BusinessNode name(String name) {
		return properties().name(name);
	}
	@Deprecated
	public <C extends Serializable> C getProperty(SinglePropertyModel<C> propertyModel) {
		return properties().get(propertyModel);
	}
	@Deprecated
	public <C extends Serializable> BusinessNode property(SinglePropertyModel<C> propertyModel, C value) {
		return properties().set(propertyModel, value);
	}
	public PropertiesNode properties() {
		return propertiesNode;
	}
	
	@Deprecated
	public BusinessNode primaryParentRef(NodeReference parentRef) {
		return assocs().primaryParentRef(parentRef);
	}
	public AssociationsNode assocs() {
		return associationsNode;
	}

	@Deprecated
	public BusinessNode content(Object content) {
		return contents().set(content);
	}
	public ContentsNode contents() {
		return contentsNode;
	}

	public PermissionsNode permissions() {
		return permissionsNode;
	}

}
