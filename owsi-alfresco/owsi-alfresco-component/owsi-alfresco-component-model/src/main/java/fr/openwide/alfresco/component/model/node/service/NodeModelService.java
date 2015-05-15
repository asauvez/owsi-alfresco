package fr.openwide.alfresco.component.model.node.service;

import java.io.OutputStream;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.api.core.node.binding.NodeContentSerializationParameters;
import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;

public interface NodeModelService {

	BusinessNode get(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;

	RepositoryContentData getNodeContent(NodeReference nodeReference, OutputStream out);
	RepositoryContentData getNodeContent(NodeReference nodeReference, ContentPropertyModel property, OutputStream out);

	List<BusinessNode> getChildren(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeScopeBuilder nodeScopeBuilder);

	List<BusinessNode> getTargetAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> getSourceAssocs(NodeReference nodeReference, AssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);

	NodeReference createFolder(NodeReference parent, String folderName) throws DuplicateChildNodeNameRemoteException;
	NodeReference createContent(NodeReference parent, String fileName, String mimeType, String encoding, Object content) throws DuplicateChildNodeNameRemoteException;

	NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNodeNameRemoteException;

	NodeReference create(BusinessNode node) throws DuplicateChildNodeNameRemoteException;
	List<NodeReference> create(List<BusinessNode> nodes) throws DuplicateChildNodeNameRemoteException;
	NodeReference create(BusinessNode node, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;
	List<NodeReference> create(List<BusinessNode> nodes, NodeContentSerializationParameters parameters) throws DuplicateChildNodeNameRemoteException;

	void update(BusinessNode node) throws DuplicateChildNodeNameRemoteException;
	void update(BusinessNode node, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNodeNameRemoteException;
	void update(List<BusinessNode> nodes, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNodeNameRemoteException;

	void delete(NodeReference nodeReference);
	void delete(List<NodeReference> nodeReferences);

}
