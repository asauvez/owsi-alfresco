package fr.openwide.alfresco.api.core.node.service;

import java.util.List;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.node.exception.NoSuchNodeRemoteException;
import fr.openwide.alfresco.api.core.node.model.NodeScope;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

public interface NodeRemoteService {

	public static final NameReference PARENT_ASSOCIATION_NAME_HINT = NameReference.create("http://www.openwide.fr/owsi-alfresco", "parentAssociationNameHint"); 
	
	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/node/get")
	class GET_NODE_SERVICE extends WebScriptParam<RepositoryNode> {
		public NodeReference nodeReference;
		public NodeScope nodeScope;
	}
	RepositoryNode get(NodeReference nodeReference, NodeScope nodeScope) throws NoSuchNodeRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/node/children")
	class CHILDREN_NODE_SERVICE extends WebScriptParam<List<RepositoryNode>> {
		public NodeReference nodeReference;
		public NameReference childAssocTypeName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getChildren(NodeReference nodeReference, NameReference childAssocTypeName, NodeScope nodeScope);

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/node/targetassoc")
	class TARGET_ASSOC_NODE_SERVICE extends WebScriptParam<List<RepositoryNode>> {
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getTargetAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope);

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/node/sourceassoc")
	class SOURCE_ASSOC_NODE_SERVICE extends WebScriptParam<List<RepositoryNode>> {
		public NodeReference nodeReference;
		public NameReference assocName; 
		public NodeScope nodeScope;
	}
	List<RepositoryNode> getSourceAssocs(NodeReference nodeReference, NameReference assocName, NodeScope nodeScope);

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/node/create")
	class CREATE_NODE_SERVICE extends WebScriptParam<List<NodeReference>> {
		public List<RepositoryNode> nodes;
	}
	List<NodeReference> create(List<RepositoryNode> nodes) throws DuplicateChildNodeNameRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/node/update")
	class UPDATE_NODE_SERVICE extends WebScriptParam<Void> {
		public List<RepositoryNode> nodes;
		public NodeScope nodeScope;
	}
	void update(List<RepositoryNode> nodes, NodeScope nodeScope) throws DuplicateChildNodeNameRemoteException;

	@WebScriptEndPoint(method=WebScriptMethod.POST, url="/owsi/node/delete")
	class DELETE_NODE_SERVICE extends WebScriptParam<Void> {
		public List<NodeReference> nodeReferences;
	}
	void delete(List<NodeReference> nodeReferences);

}
