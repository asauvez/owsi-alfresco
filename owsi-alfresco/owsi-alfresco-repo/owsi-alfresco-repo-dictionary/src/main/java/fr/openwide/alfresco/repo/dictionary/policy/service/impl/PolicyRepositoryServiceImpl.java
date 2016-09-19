package fr.openwide.alfresco.repo.dictionary.policy.service.impl;

import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnMoveNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.AssociationPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class PolicyRepositoryServiceImpl implements PolicyRepositoryService {

	private PolicyComponent policyComponent;

	private ConversionService conversionService;
	
	@Override
	public <T extends ClassPolicy> void bindClassBehaviour(ContainerModel type, NotificationFrequency frequency,
			Class<T> eventType, T policy) {
		try {
			QName policyQName = (QName) eventType.getField("QNAME").get(null);
			policyComponent.bindClassBehaviour(policyQName, 
					conversionService.getRequired(type.getNameReference()), 
					new JavaBehaviour(policy, policyQName.getLocalName(), frequency));
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new IllegalStateException(ex);
		}
	}
	@Override
	public <T extends AssociationPolicy> void bindAssociationBehaviour(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency,
			Class<T> eventType, T policy) {
		try {
			QName policyQName = (QName) eventType.getField("QNAME").get(null);
			policyComponent.bindAssociationBehaviour(policyQName, 
					conversionService.getRequired(type.getNameReference()),
					conversionService.getRequired(association.getNameReference()), 
					new JavaBehaviour(policy, policyQName.getLocalName(), frequency));
		} catch (IllegalAccessException | NoSuchFieldException ex) {
			throw new IllegalStateException(ex);
		}
	}
	
	@Override
	public void onAddAspect(ContainerModel type, NotificationFrequency frequency, OnAddAspectPolicy policy) {
		bindClassBehaviour(type, frequency, OnAddAspectPolicy.class, policy);
	}
	@Override
	public void onRemoveAspect(ContainerModel type, NotificationFrequency frequency, OnRemoveAspectPolicy policy) {
		bindClassBehaviour(type, frequency, OnRemoveAspectPolicy.class, policy);
	}

	@Override
	public void onUpdateProperties(ContainerModel type, NotificationFrequency frequency, OnUpdatePropertiesPolicy policy) {
		bindClassBehaviour(type, frequency, OnUpdatePropertiesPolicy.class, policy);
	}
	
	@Override
	public void onCreateNodePolicy(ContainerModel type, NotificationFrequency frequency, OnCreateNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnCreateNodePolicy.class, policy);
	}
	@Override
	public void onMoveNodePolicy(ContainerModel type, NotificationFrequency frequency, OnMoveNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnMoveNodePolicy.class, policy);
	}
	@Override
	public void onDeleteNodePolicy(ContainerModel type, NotificationFrequency frequency, OnDeleteNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnDeleteNodePolicy.class, policy);
	}
	@Override
	public void onCreateChildAssociationPolicy(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency, OnCreateChildAssociationPolicy policy) {
		bindAssociationBehaviour(type, association, frequency, OnCreateChildAssociationPolicy.class, policy);
	}
	@Override
	public void onDeleteChildAssociationPolicy(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency, OnDeleteChildAssociationPolicy policy) {
		bindAssociationBehaviour(type, association, frequency, OnDeleteChildAssociationPolicy.class, policy);
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
