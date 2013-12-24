package fr.openwide.alfresco.component.model.node.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.component.model.node.model.property.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

/**
 * Contient les informations liées à une node. 
 * @author asauvez
 */
public class BusinessNode {

	private RepositoryNode node;

	public BusinessNode() {
		this(new RepositoryNode());
	}

	public BusinessNode(RepositoryNode node) {
		this.node = node;
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

	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(PropertyModel<C> propertyModel) {
		return (C) node.getProperties().get(propertyModel.getNameReference());
	}
	public <C extends Serializable> BusinessNode property(PropertyModel<C> propertyModel, C value) {
		node.getProperties().put(propertyModel.getNameReference(), value);
		return this;
	}
	
	public String getContentString() {
		return getContentString(CmModel.content.content);
	}
	public String getContentString(ContentPropertyModel propertyModel) {
		return node.getContentStrings().get(propertyModel);
	}
	
	public String getName() {
		return getProperty(CmModel.object.name);
	}
	public BusinessNode name(String name) {
		property(CmModel.object.name, name);
		return this;
	}

	public boolean hasUserPermission(RepositoryPermission permission) {
		return node.getUserPermissions().contains(permission);
	}
	public BusinessNode userPermission(RepositoryPermission permission) {
		node.getUserPermissions().add(permission);
		return this;
	}

	public BusinessNode getPrimaryParent() {
		return new BusinessNode(node.getPrimaryParent());
	}
	public BusinessNode primaryParentRef(NodeReference parentRef) {
		primaryParent().nodeReference(parentRef);
		return this;
	}
	public BusinessNode primaryParent() {
		BusinessNode primaryParent = new BusinessNode();
		node.setPrimaryParent(primaryParent.node);
		return primaryParent;
	}

	public List<BusinessNode> getChildAssociationContains() {
		return getChildAssociation(CmModel.folder.contains);
	}
	public List<BusinessNode> getChildAssociation(ChildAssociationModel childAssociation) {
		return wrapList(node.getChildAssociations().get(childAssociation.getNameReference()));
	}
	public List<BusinessNode> getTargetAssociation(AssociationModel association) {
		return wrapList(node.getTargetAssocs().get(association.getNameReference()));
	}
	public List<BusinessNode> getSourceAssociation(AssociationModel association) {
		return wrapList(node.getSourceAssocs().get(association.getNameReference()));
	}
	
	public static List<BusinessNode> wrapList(List<RepositoryNode> nodes) {
		ArrayList<BusinessNode> wrappers = new ArrayList<>();
		for (RepositoryNode node : nodes) {
			wrappers.add(new BusinessNode(node));
		}
		return wrappers;
	}

}
