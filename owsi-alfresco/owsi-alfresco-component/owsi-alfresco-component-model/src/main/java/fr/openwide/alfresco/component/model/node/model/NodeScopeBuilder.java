package fr.openwide.alfresco.component.model.node.model;

import java.io.File;

import javax.servlet.http.HttpServletResponse;

import fr.openwide.alfresco.app.core.node.binding.ByteArrayRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.FolderRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.HttpServletResponseRepositoryContentDeserializer;
import fr.openwide.alfresco.app.core.node.binding.StringRepositoryContentSerializer;
import fr.openwide.alfresco.app.core.node.binding.TempFileRepositoryContentSerializer;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.model.NodeScope;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.node.model.RepositoryPermission;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

/**
 * Indique les données liés à un noeud à rapporter lors d'une recherche. 
 *  
 * @author asauvez
 */
public class NodeScopeBuilder {

	private NodeScope scope = new NodeScope();

	public NodeScope getScope() {
		return scope;
	}

	public NodeScopeBuilder fromNode(BusinessNode node) {
		RepositoryNode repositoryNode = node.getRepositoryNode();
		if (repositoryNode.getType() != null) type();
		if (node.getRepositoryNode().getPrimaryParentAssociation() != null) primaryParent();
		
		scope.getProperties().addAll(repositoryNode.getProperties().keySet());
		for (NameReference contentProperty : repositoryNode.getContents().keySet()) {
			// Le nodeScope sera envoyé à Alfresco sans le deserializer. Donc on peut mettre null.
			scope.getContentDeserializers().put(contentProperty, null);
		}
		return this;
	}
	
	public NodeScopeBuilder nodeReference() {
		scope.setNodeReference(true);
		return this;
	}

	public NodeScopeBuilder path() {
		scope.setPath(true);
		return this;
	}

	public NodeScopeBuilder type() {
		scope.setType(true);
		return this;
	}

	public NodeScopeBuilder name() {
		return property(CmModel.object.name);
	}
	public NodeScopeBuilder property(PropertyModel<?> propertyModel) {
		scope.getProperties().add(propertyModel.getNameReference());
		return this;
	}
	public NodeScopeBuilder properties(ContainerModel type) {
		for (PropertyModel<?> property : type.getProperties().values()) {
			property(property);
		}
		return this;
	}

	public NodeScopeBuilder contentAsString() {
		return contentWithSerializer(StringRepositoryContentSerializer.INSTANCE);
	}
	public NodeScopeBuilder contentAsByteArray() {
		return contentWithSerializer(ByteArrayRepositoryContentSerializer.INSTANCE);
	}
	public NodeScopeBuilder contentAsTempFile() {
		return contentWithSerializer(TempFileRepositoryContentSerializer.INSTANCE);
	}
	public NodeScopeBuilder contentAsFilesInFolder(File destinationFolder) {
		return contentWithSerializer(new FolderRepositoryContentSerializer(destinationFolder));
	}
	
	public NodeScopeBuilder contentAsInlineHttpResponse(HttpServletResponse response) {
		return contentAsDownloadHttpResponse(response);
	}
	public NodeScopeBuilder contentAsDownloadHttpResponse(HttpServletResponse response) {
		this.name(); // on va avoir besoin du cm:name
		return contentWithSerializer(new HttpServletResponseRepositoryContentDeserializer(response, CmModel.object.name.getNameReference()));
	}
	public NodeScopeBuilder contentAsDownloadHttpResponse(HttpServletResponse response, String fileName) {
		return contentWithSerializer(new HttpServletResponseRepositoryContentDeserializer(response, fileName));
	}
	
	public NodeScopeBuilder contentWithSerializer(RepositoryContentDeserializer<?> deserializer) {
		return contentWithSerializer(CmModel.content.content, deserializer);
	}
	public NodeScopeBuilder contentWithSerializer(PropertyModel<?> propertyModel, RepositoryContentDeserializer<?> deserializer) {
		scope.getProperties().add(propertyModel.getNameReference());
		scope.getContentDeserializers().put(propertyModel.getNameReference(), deserializer);
		return this;
	}

	public NodeScopeBuilder aspect(AspectModel aspectModel) {
		scope.getAspects().add(aspectModel.getNameReference());
		return this;
	}

	public NodeScopeBuilder userPermission(RepositoryPermission permission) {
		scope.getUserPermissions().add(permission);
		return this;
	}
	public NodeScopeBuilder accessPermissions() {
		scope.setAccessPermissions(true);
		return this;
	}
	

	public NodeScopeBuilder primaryParent() {
		NodeScopeBuilder primaryParent = new NodeScopeBuilder();
		scope.setPrimaryParent(primaryParent.scope);
		return primaryParent;
	}
	
	public NodeScopeBuilder childAssociationContains() {
		return childAssociation(CmModel.folder.contains);
	}
	public NodeScopeBuilder childAssociation(ChildAssociationModel childAssociation) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getChildAssociations().put(childAssociation.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder parentAssociationContains() {
		return parentAssociation(CmModel.folder.contains);
	}
	public NodeScopeBuilder parentAssociation(ChildAssociationModel childAssociation) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getParentAssociations().put(childAssociation.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder targetAssociation(AssociationModel association) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getTargetAssocs().put(association.getNameReference(), other.getScope());
		return other;
	}
	public NodeScopeBuilder sourceAssociation(AssociationModel association) {
		NodeScopeBuilder other = new NodeScopeBuilder();
		scope.getSourceAssocs().put(association.getNameReference(), other.getScope());
		return other;
	}

	public NodeScopeBuilder recursiveChildAssociationsContains() {
		return recursiveChildAssociations(CmModel.folder.contains);
	}
	public NodeScopeBuilder recursiveChildAssociations(ChildAssociationModel childAssociation) {
		scope.getRecursiveChildAssociations().add(childAssociation.getNameReference());
		return this;
	}
	public NodeScopeBuilder recursiveParentAssociationsContains() {
		return recursiveParentAssociations(CmModel.folder.contains);
	}
	public NodeScopeBuilder recursiveParentAssociations(ChildAssociationModel childAssociation) {
		scope.getRecursiveParentAssociations().add(childAssociation.getNameReference());
		return this;
	}

}
