package fr.openwide.alfresco.component.model.node.model;

import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.embed.AssociationsNode;
import fr.openwide.alfresco.component.model.node.model.embed.ContentsNode;
import fr.openwide.alfresco.component.model.node.model.embed.PermissionsNode;
import fr.openwide.alfresco.component.model.node.model.embed.PropertiesNode;
import fr.openwide.alfresco.component.model.node.model.embed.RenditionsNode;

public class BusinessNode {

	private RepositoryNode node;
	private PropertiesNode propertiesNode;
	private AssociationsNode associationsNode;
	private RenditionsNode renditionsNode;
	private PermissionsNode permissionsNode;
	private ContentsNode contentsNode;
	
	public BusinessNode(RepositoryNode node) {
		this.node = node;
		propertiesNode = new PropertiesNode(this);
		associationsNode = new AssociationsNode(this);
		renditionsNode = new RenditionsNode(this);
		permissionsNode = new PermissionsNode(this);
		contentsNode = new ContentsNode(this);
	}

	public BusinessNode() {
		this(new RepositoryNode());
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

	public PropertiesNode properties() {
		return propertiesNode;
	}
	
	public AssociationsNode assocs() {
		return associationsNode;
	}
	
	public RenditionsNode renditions() {
		return renditionsNode;
	}

	public ContentsNode contents() {
		return contentsNode;
	}

	public PermissionsNode permissions() {
		return permissionsNode;
	}

}
