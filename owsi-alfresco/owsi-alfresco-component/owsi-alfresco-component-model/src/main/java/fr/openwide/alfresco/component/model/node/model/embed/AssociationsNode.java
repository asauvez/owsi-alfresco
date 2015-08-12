package fr.openwide.alfresco.component.model.node.model.embed;

import java.util.ArrayList;
import java.util.List;

import fr.openwide.alfresco.api.core.node.model.RepositoryChildAssociation;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.BusinessNodeList;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class AssociationsNode {

	private final BusinessNode node;
	private final RepositoryNode repoNode;
	
	public AssociationsNode(BusinessNode node) {
		this.node = node;
		this.repoNode = node.getRepositoryNode();
	}
	

	public BusinessNode getPrimaryParent() {
		return (repoNode.getPrimaryParentAssociation() != null) 
				? new BusinessNode(repoNode.getPrimaryParentAssociation().getParentNode())
				: null;
	}
	public BusinessNode primaryParentRef(NodeReference parentRef) {
		primaryParent().nodeReference(parentRef);
		return node;
	}
	public BusinessNode primaryParent() {
		return primaryParent(CmModel.folder.contains);
	}
	public BusinessNode primaryParent(ChildAssociationModel childAssociationModel) {
		if (repoNode.getPrimaryParentAssociation() == null) {
			repoNode.setPrimaryParentAssociation(new RepositoryChildAssociation(
					new RepositoryNode(), 
					childAssociationModel.getNameReference()));
		}
		return new BusinessNode(repoNode.getPrimaryParentAssociation().getParentNode());
	}
	public boolean isPrimaryParentAssociation(ChildAssociationModel childAssociationModel) {
		return childAssociationModel.getNameReference().equals(repoNode.getPrimaryParentAssociation().getType());
	}

	public BusinessNode getRendition(NameReference renditionName) {
		RepositoryNode renditionNode = repoNode.getRenditions().get(renditionName);
		return (renditionNode != null) ? new BusinessNode(renditionNode) : null;
	}
	
	public List<BusinessNode> getChildAssociationContains() {
		return getChildAssociation(CmModel.folder.contains);
	}
	public List<BusinessNode> getChildAssociation(ChildAssociationModel childAssociation) {
		List<RepositoryNode> list = repoNode.getChildAssociations().get(childAssociation.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			repoNode.getChildAssociations().put(childAssociation.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}
	public List<BusinessNode> getParentAssociation(ChildAssociationModel childAssociation) {
		List<RepositoryNode> list = repoNode.getParentAssociations().get(childAssociation.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			repoNode.getParentAssociations().put(childAssociation.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}
	public List<BusinessNode> getTargetAssociation(AssociationModel association) {
		List<RepositoryNode> list = repoNode.getTargetAssocs().get(association.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			repoNode.getTargetAssocs().put(association.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}
	public List<BusinessNode> getSourceAssociation(AssociationModel association) {
		List<RepositoryNode> list = repoNode.getSourceAssocs().get(association.getNameReference());
		if (list == null) {
			list = new ArrayList<>();
			repoNode.getSourceAssocs().put(association.getNameReference(), list);
		}
		return new BusinessNodeList(list);
	}

}
