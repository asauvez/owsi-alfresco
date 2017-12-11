package fr.openwide.alfresco.component.model.node.service;

import java.nio.charset.Charset;
import java.util.List;
import java.util.Optional;

import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToOneAssociationModel;

public interface NodeModelService {

	BusinessNode get(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder) throws NoSuchNodeRemoteException;

	List<BusinessNode> getChildren(NodeReference nodeReference, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> getChildren(NodeReference nodeReference, ChildAssociationModel childAssoc, NodeScopeBuilder nodeScopeBuilder);

	List<BusinessNode> getTargetAssocs(NodeReference nodeReference, ManyToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> getSourceAssocs(NodeReference nodeReference, ManyToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);

	Optional<BusinessNode> getTargetAssocs(NodeReference nodeReference, ManyToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);
	List<BusinessNode> getSourceAssocs(NodeReference nodeReference, ManyToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);

	List<BusinessNode> getTargetAssocs(NodeReference nodeReference, OneToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);
	Optional<BusinessNode> getSourceAssocs(NodeReference nodeReference, OneToManyAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);

	Optional<BusinessNode> getTargetAssocs(NodeReference nodeReference, OneToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);
	Optional<BusinessNode> getSourceAssocs(NodeReference nodeReference, OneToOneAssociationModel assoc, NodeScopeBuilder nodeScopeBuilder);

	NodeReference createFolder(NodeReference parentRef, String folderName) throws DuplicateChildNodeNameRemoteException;
	NodeReference createContent(NodeReference parentRef, String fileName, String mimeType, String encoding, Object content) throws DuplicateChildNodeNameRemoteException;
	NodeReference createContent(NodeReference parentRef, String fileName, MediaType mimeType, Charset encoding, Object content) throws DuplicateChildNodeNameRemoteException;

	NodeReference createContent(NodeReference parent, MultipartFile file) throws DuplicateChildNodeNameRemoteException;

	NodeReference create(BusinessNode node) throws DuplicateChildNodeNameRemoteException;
	List<NodeReference> create(List<BusinessNode> nodes) throws DuplicateChildNodeNameRemoteException;

	void update(BusinessNode node) throws DuplicateChildNodeNameRemoteException;
	void update(BusinessNode node, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNodeNameRemoteException;
	void update(List<BusinessNode> nodes, NodeScopeBuilder nodeScopeBuilder) throws DuplicateChildNodeNameRemoteException;

	void delete(NodeReference nodeReference);
	void delete(List<NodeReference> nodeReferences);

}
