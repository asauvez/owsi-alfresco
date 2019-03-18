package fr.openwide.alfresco.repo.module.deleteifempty.service.impl;

import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnMoveNodePolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class DeleteIfEmptyServiceImpl implements InitializingBean, 
		OnDeleteChildAssociationPolicy, OnMoveNodePolicy, OnDeleteNodePolicy {
	
	@Autowired private NodeModelRepositoryService nodeModelService;
	private PolicyRepositoryService policyRepositoryService;
	private ConversionService conversionService;

	@Override
	public void afterPropertiesSet() throws Exception {
		policyRepositoryService.onDeleteNode(CmModel.object, NotificationFrequency.TRANSACTION_COMMIT, this);
		policyRepositoryService.onMoveNode(CmModel.object, NotificationFrequency.TRANSACTION_COMMIT, this);
		policyRepositoryService.onDeleteChildAssociation(OwsiModel.deleteIfEmpty, CmModel.folder.contains, NotificationFrequency.TRANSACTION_COMMIT, this);
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		NodeReference parentRef = conversionService.get(childAssocRef.getParentRef());
		
		if (nodeModelService.exists(parentRef) && nodeModelService.hasAspect(parentRef, OwsiModel.deleteIfEmpty)) {
			onDeleteChildAssociation(childAssocRef);
		}
	}
	
	@Override
	public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
		onDeleteNode(oldChildAssocRef, true);
	}
	
	@Override
	public void onDeleteChildAssociation(ChildAssociationRef childAssocRef) {
		NodeReference parentRef = conversionService.get(childAssocRef.getParentRef());
		
		// Si le noeud parent a encore d'autres enfants
		if (   nodeModelService.exists(parentRef) 
			&& nodeModelService.getChildrenAssocsContains(parentRef).isEmpty()) {
			
			nodeModelService.deleteNode(parentRef);
		}
	}
	
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setPolicyRepositoryService(PolicyRepositoryService policyRepositoryService) {
		this.policyRepositoryService = policyRepositoryService;
	}
}
