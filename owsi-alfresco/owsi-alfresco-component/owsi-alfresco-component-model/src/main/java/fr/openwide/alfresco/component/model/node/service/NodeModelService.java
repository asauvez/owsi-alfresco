package fr.openwide.alfresco.component.model.node.service;

import java.io.OutputStream;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.repository.api.node.exception.DuplicateChildNameException;
import fr.openwide.alfresco.repository.api.node.exception.NoSuchNodeException;
import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.node.serializer.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

/**
 * Permet de manipuler les noeuds.
 * 
 * @author asauvez
 */
public interface NodeModelService {

	BusinessNode get(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeException;

	RepositoryContentData getNodeContent(NodeReference nodeReference, OutputStream out);
	RepositoryContentData getNodeContent(NodeReference nodeReference, ContentPropertyModel property, OutputStream out);

	
	List<BusinessNode> getChildren(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeScopeBuilder nodeScopeBuilder);

	List<BusinessNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);

	NodeReference createFolder(NodeReference parent, String folderName) throws DuplicateChildNameException;
	NodeReference createContent(NodeReference parent, String fileName, String mimeType, String encoding, Object content) throws DuplicateChildNameException;

	NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNameException;

	NodeReference create(BusinessNode node) throws DuplicateChildNameException;
	List<NodeReference> create(List<BusinessNode> nodes) throws DuplicateChildNameException;
	NodeReference create(BusinessNode node, RepositoryContentSerializer<?> serializer) throws DuplicateChildNameException;
	NodeReference create(BusinessNode node, Map<ContentPropertyModel, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException;
	List<NodeReference> create(List<BusinessNode> nodes, Map<ContentPropertyModel, RepositoryContentSerializer<?>> serializers) throws DuplicateChildNameException;
	
	void update(BusinessNode node) throws DuplicateChildNameException;
	void update(BusinessNode node, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNameException;
	void update(List<BusinessNode> nodes, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNameException;
	
	void delete(NodeReference nodeReference);

}
