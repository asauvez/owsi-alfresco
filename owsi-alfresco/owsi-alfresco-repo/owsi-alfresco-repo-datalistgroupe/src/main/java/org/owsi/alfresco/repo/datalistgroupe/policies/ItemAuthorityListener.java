package org.owsi.alfresco.repo.datalistgroupe.policies;

import java.util.List;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.owsi.alfresco.repo.datalistgroupe.model.DatalistAuthorityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ItemAuthorityListener implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy,
																NodeServicePolicies.OnDeleteAssociationPolicy {
	
	private final Logger LOGGER = LoggerFactory.getLogger(DatalistAuthorityListener.class);

	// Dependencies
	private NodeService nodeService;
	private PolicyComponent policyComponent;
	private AuthorityService authorityService;
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}

	// Behaviours
	private Behaviour onCreateNode;
	private Behaviour beforeDeleteNode;
	private Behaviour onDeleteAssociation;

	
	public void init() {

		// Create behaviours
		this.onCreateNode = new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT);
		this.beforeDeleteNode = new JavaBehaviour(this, "beforeDeleteNode", NotificationFrequency.FIRST_EVENT);
		this.onDeleteAssociation = new JavaBehaviour(this, "onDeleteAssociation", NotificationFrequency.TRANSACTION_COMMIT);
		
		// Bind behaviours to node policies
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DatalistAuthorityModel.TYPE_DATALISTAUTHORITY_ITEM,
				this.onCreateNode
				);
		
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DatalistAuthorityModel.TYPE_DATALISTAUTHORITY_ITEM,
				this.beforeDeleteNode
				);
		
		this.policyComponent.bindAssociationBehaviour(NodeServicePolicies.OnDeleteAssociationPolicy.QNAME,
				DatalistAuthorityModel.TYPE_DATALISTAUTHORITY_ITEM, DatalistAuthorityModel.ASSOC_DATALISTAUTHORITY,
				this.onDeleteAssociation);
	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		final NodeRef finalNodeRef = nodeRef;
		
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			
			@Override
			public Object doWork() throws Exception {
				if(nodeService.exists(finalNodeRef)) {
					ChildAssociationRef childAssociationRef = nodeService.getPrimaryParent(finalNodeRef); 
					NodeRef parentNodeRef = childAssociationRef.getParentRef();
					List<AssociationRef> associations = nodeService.getTargetAssocs(finalNodeRef, DatalistAuthorityModel.ASSOC_DATALISTAUTHORITY);
					if(! associations.isEmpty()) {
						NodeRef targetAssoc =  associations.get(0).getTargetRef();
						if(nodeService.exists(targetAssoc)) {
							String authorityName = getAuthorityName(targetAssoc);
							String authorityNameGroup = (String) nodeService.getProperty(parentNodeRef, DatalistAuthorityModel.PROP_DATALISTAUTHORITY_GROUP_NAME);
							if (! authorityService.authorityExists(authorityNameGroup)) {
								LOGGER.warn(authorityNameGroup + " n'existe pas !");
							} else {
									authorityService.removeAuthority(authorityNameGroup,authorityName );
							}
						}
					}
				}
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());
	}

	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {

		final NodeRef finalNodeRef = childAssocRef.getChildRef();
		final NodeRef finalParentNodeRef = childAssocRef.getParentRef();

		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				if((nodeService.exists(finalNodeRef)) && (nodeService.exists(finalParentNodeRef))) {

					List<AssociationRef> associations = nodeService.getTargetAssocs(finalNodeRef, DatalistAuthorityModel.ASSOC_DATALISTAUTHORITY);
					NodeRef targetAssoc =  associations.get(0).getTargetRef();
					
					// Add the authority to the group
					if(nodeService.exists(targetAssoc)) {
						String authorityNameGroup = (String) nodeService.getProperty(finalParentNodeRef, DatalistAuthorityModel.PROP_DATALISTAUTHORITY_GROUP_NAME);
						String authorityName = getAuthorityName(targetAssoc);
						
						if (authorityService.authorityExists(authorityNameGroup)) {
							authorityService.addAuthority(authorityNameGroup, authorityName);
							nodeService.setProperty(finalNodeRef, DatalistAuthorityModel.PROP_DATALISTAUTHORITY_NAME, authorityName);
						} else {
							throw new IllegalArgumentException("Echec ajout authority, le groupe " + authorityNameGroup + " n'éxiste pas");
							}
					}
				}
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());
	}

	private String getAuthorityName(NodeRef nodeRef) {
		return (String) (ContentModel.TYPE_PERSON.equals(nodeService.getType(nodeRef)) ? 
				nodeService.getProperty(nodeRef, ContentModel.PROP_USERNAME) : 
				nodeService.getProperty(nodeRef, ContentModel.PROP_AUTHORITY_NAME));
	}

	@Override
	public void onDeleteAssociation(AssociationRef nodeAssocRef) {

		final NodeRef sourceNodeRef = nodeAssocRef.getSourceRef();
		final NodeRef targetNodeRef = nodeAssocRef.getTargetRef();

		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {

			@Override
			public Object doWork() throws Exception {
				if(nodeService.exists(sourceNodeRef) && ! nodeService.exists(targetNodeRef)) {
					nodeService.deleteNode(sourceNodeRef);
				}
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());
	}
}
