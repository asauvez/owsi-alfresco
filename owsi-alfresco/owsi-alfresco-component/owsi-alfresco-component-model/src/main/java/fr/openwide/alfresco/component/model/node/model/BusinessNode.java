package fr.openwide.alfresco.component.model.node.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;

import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthority;
import fr.openwide.alfresco.repository.api.node.model.RepositoryAuthorityPermission;
import fr.openwide.alfresco.repository.api.node.model.RepositoryChildAssociation;
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
		return (C) node.getProperties().get(propertyModel.getNameReference());
	}
	public <C extends Serializable> BusinessNode property(SinglePropertyModel<C> propertyModel, C value) {
		node.getProperties().put(propertyModel.getNameReference(), value);
		return this;
	}

	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(MultiPropertyModel<C> propertyModel) {
		return (List<C>) node.getProperties().get(propertyModel.getNameReference());
	}
	public <C extends Serializable> BusinessNode property(MultiPropertyModel<C> propertyModel, Collection<C> value) {
		node.getProperties().put(propertyModel.getNameReference(), (Serializable) value); 
		return this;
	}
	public <C extends Serializable> BusinessNode property(MultiPropertyModel<C> propertyModel, @SuppressWarnings("unchecked") C ... values) {
		return property(propertyModel, Arrays.asList(values));
	}

	public String getContentString() {
		return getContentString(CmModel.content.content);
	}
	public String getContentString(ContentPropertyModel propertyModel) {
		return node.getContentStrings().get(propertyModel.getNameReference());
	}
	public BusinessNode contentString(String content) {
		return contentString(CmModel.content.content, content);
	}
	public BusinessNode contentString(ContentPropertyModel property, String content) {
		node.getContentStrings().put(property.getNameReference(), content);
		return this;
	}
	

	public BusinessNode contentResource(File file) {
		return contentResource(new FileSystemResource(file));
	}
	public BusinessNode contentResource(InputStream input, final long contentLength) {
		return contentResource(new InputStreamResource(input) {
			@Override
			public long contentLength() throws IOException {
				return contentLength;
			}
			@Override
			public String getFilename() {
				return BusinessNode.this.getName();
			}
		});
	}
	public BusinessNode contentResource(Resource content) {
		return contentResource(CmModel.content.content, content);
	}
	public BusinessNode contentResource(ContentPropertyModel property, Resource content) {
		node.getContentResources().put(property.getNameReference(), content);
		return this;
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
	
	public Boolean getInheritParentPermissions() {
		return node.getInheritParentPermissions();
	}
	public BusinessNode inheritParentPermissions(Boolean inheritParentPermissions) {
		node.setInheritParentPermissions(inheritParentPermissions);
		return this;
	}
	
	public Set<RepositoryAuthorityPermission> getAccessPermissions() {
		return node.getAccessPermissions();
	}
	public BusinessNode addAccessPermission(RepositoryAuthority authority, RepositoryPermission permission, boolean allowed) {
		node.getAccessPermissions().add(new RepositoryAuthorityPermission(authority, permission, allowed));
		return this;
	}

	public BusinessNode getPrimaryParent() {
		return new BusinessNode(node.getPrimaryParentAssociation().getParentNode());
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
