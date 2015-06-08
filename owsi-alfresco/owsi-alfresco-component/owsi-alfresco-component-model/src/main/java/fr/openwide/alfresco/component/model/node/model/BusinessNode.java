package fr.openwide.alfresco.component.model.node.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.node.model.RepositoryAccessControl;
import fr.openwide.alfresco.api.core.node.model.RepositoryChildAssociation;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.node.model.RepositoryPermission;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class BusinessNode {

	private RepositoryNode node;

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
		primaryParentRef(parentRef);
		type(type);
		name(name);
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

	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(SinglePropertyModel<C> propertyModel) {
		return (C) node.getProperty(propertyModel.getNameReference());
	}
	public <C extends Serializable> BusinessNode property(SinglePropertyModel<C> propertyModel, C value) {
		node.getProperties().put(propertyModel.getNameReference(), value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(MultiPropertyModel<C> propertyModel) {
		return (List<C>) node.getProperty(propertyModel.getNameReference());
	}
	public <C extends Serializable> BusinessNode property(MultiPropertyModel<C> propertyModel, Collection<C> value) {
		node.getProperties().put(propertyModel.getNameReference(), (Serializable) value); 
		return this;
	}
	public <C extends Serializable> BusinessNode property(MultiPropertyModel<C> propertyModel, @SuppressWarnings("unchecked") C ... values) {
		return property(propertyModel, Arrays.asList(values));
	}

	public Object getContent() {
		return getContent(CmModel.content.content);
	}
	public Object getContent(ContentPropertyModel propertyModel) {
		return node.getContents().get(propertyModel.getNameReference());
	}
	public BusinessNode content(Object content) {
		return content(CmModel.content.content, content);
	}
	public BusinessNode content(ContentPropertyModel property, Object content) {
		return content(property, null, content);
	}
	public BusinessNode content(RepositoryContentData contentData, Object content) {
		return content(CmModel.content.content, contentData, content);
	}
	public BusinessNode content(ContentPropertyModel property, RepositoryContentData contentData, Object content) {
		node.getContents().put(property.getNameReference(), content);
		if (contentData != null) {
			node.getProperties().put(property.getNameReference(), contentData);
		}
		return this;
	}

	public String getName() {
		return getProperty(CmModel.object.name);
	}
	public BusinessNode name(String name) {
		property(CmModel.object.name, name);
		return this;
	}
	
	public String getTitle() {
		return getProperty(CmModel.titled.title);
	}
	public BusinessNode title(String name) {
		property(CmModel.titled.title, name);
		return this;
	}
	public String getDescription() {
		return getProperty(CmModel.titled.description);
	}
	public BusinessNode description(String name) {
		property(CmModel.titled.description, name);
		return this;
	}

	public boolean hasUserPermission(RepositoryPermission permission) {
		return node.getUserPermissions().contains(permission);
	}
	public BusinessNode userPermission(RepositoryPermission permission) {
		node.getUserPermissions().add(permission);
		return this;
	}
	
	public Boolean getInheritParentPermissions() {
		return node.getInheritParentPermissions();
	}
	public BusinessNode inheritParentPermissions(Boolean inheritParentPermissions) {
		node.setInheritParentPermissions(inheritParentPermissions);
		return this;
	}
	
	public Set<RepositoryAccessControl> getAccessControlList() {
		return node.getAccessControlList();
	}
	public BusinessNode addAccessControl(RepositoryAuthority authority, RepositoryPermission permission, boolean allowed) {
		node.getAccessControlList().add(new RepositoryAccessControl(authority, permission, allowed));
		return this;
	}

	public BusinessNode getPrimaryParent() {
		return (node.getPrimaryParentAssociation() != null) 
				? new BusinessNode(node.getPrimaryParentAssociation().getParentNode())
				: null;
	}
	public BusinessNode primaryParentRef(NodeReference parentRef) {
		primaryParent().nodeReference(parentRef);
		return this;
	}
	public BusinessNode primaryParent() {
		return primaryParent(CmModel.folder.contains);
	}
	public BusinessNode primaryParent(ChildAssociationModel childAssociationModel) {
		BusinessNode primaryParent = new BusinessNode();
		node.setPrimaryParentAssociation(new RepositoryChildAssociation(
				primaryParent.getRepositoryNode(), 
				childAssociationModel.getNameReference()));
		return primaryParent;
	}
	public boolean isPrimaryParentAssociation(ChildAssociationModel childAssociationModel) {
		return childAssociationModel.getNameReference().equals(node.getPrimaryParentAssociation().getType());
	}

	public List<BusinessNode> getChildAssociationContains() {
		return getChildAssociation(CmModel.folder.contains);
	}
	public List<BusinessNode> getChildAssociation(ChildAssociationModel childAssociation) {
		List<RepositoryNode> list = node.getChildAssociations().get(childAssociation.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			node.getChildAssociations().put(childAssociation.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}
	public List<BusinessNode> getParentAssociation(ChildAssociationModel childAssociation) {
		List<RepositoryNode> list = node.getParentAssociations().get(childAssociation.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			node.getParentAssociations().put(childAssociation.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}
	public List<BusinessNode> getTargetAssociation(AssociationModel association) {
		List<RepositoryNode> list = node.getTargetAssocs().get(association.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			node.getTargetAssocs().put(association.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}
	public List<BusinessNode> getSourceAssociation(AssociationModel association) {
		List<RepositoryNode> list = node.getSourceAssocs().get(association.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			node.getSourceAssocs().put(association.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}
	
}
