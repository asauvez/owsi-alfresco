package fr.openwide.alfresco.repo.module.deleteifempty.service.impl;

import java.util.Properties;

import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnMoveNodePolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;

public class DeleteIfEmptyServiceImpl implements InitializingBean, 
		OnDeleteChildAssociationPolicy, OnMoveNodePolicy, OnDeleteNodePolicy {
	
	@Autowired private NodeModelRepositoryService nodeModelService;
	private PolicyRepositoryService policyRepositoryService;

	@Autowired @Qualifier("global-properties")
	private Properties globalProperties;
	
	private boolean deleteNodePermanently = true;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		policyRepositoryService.onDeleteNode(CmModel.object, NotificationFrequency.TRANSACTION_COMMIT, this);
		policyRepositoryService.onMoveNode(CmModel.object, NotificationFrequency.TRANSACTION_COMMIT, this);
		policyRepositoryService.onDeleteChildAssociation(OwsiModel.deleteIfEmpty, CmModel.folder.contains, NotificationFrequency.TRANSACTION_COMMIT, this);
		
		deleteNodePermanently = Boolean.parseBoolean(globalProperties.getProperty("owsi.deleteIfEmpty.deleteNodePermanently", "true"));
	}

	@Override
	public void onDeleteNode(ChildAssociationRef childAssocRef, boolean isNodeArchived) {
		NodeRef parentRef = childAssocRef.getParentRef();
		
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
		NodeRef parentRef = childAssocRef.getParentRef();
		
		// runAsSystem pour voir les enfants que le user en cours n'a pas le droit de voir
		AuthenticationUtil.runAsSystem(() -> {
			// Si le noeud parent a encore d'autres enfants
			if (   nodeModelService.exists(parentRef) 
				&& nodeModelService.getChildrenAssocsContains(parentRef).isEmpty()) {
				
				if (deleteNodePermanently) {
					nodeModelService.deleteNodePermanently(parentRef);
				} else {
					nodeModelService.deleteNode(parentRef);
				}
			}
			return null;
		});
	}
	
	public void setPolicyRepositoryService(PolicyRepositoryService policyRepositoryService) {
		this.policyRepositoryService = policyRepositoryService;
	}
}
