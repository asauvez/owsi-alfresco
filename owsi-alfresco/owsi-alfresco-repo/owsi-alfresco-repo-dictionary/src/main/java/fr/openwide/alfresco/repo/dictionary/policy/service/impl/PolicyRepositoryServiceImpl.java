package fr.openwide.alfresco.repo.dictionary.policy.service.impl;

import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.namespace.QName;

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
	public void onAddAspect(ContainerModel type, NotificationFrequency frequency, OnAddAspectPolicy policy) {
		bindClassBehaviour(type, frequency, OnAddAspectPolicy.class, policy);
	}

	@Override
	public void onUpdateProperties(ContainerModel type, NotificationFrequency frequency, OnUpdatePropertiesPolicy policy) {
		bindClassBehaviour(type, frequency, OnUpdatePropertiesPolicy.class, policy);
	}
	
	@Override
	public void onDeleteNodePolicy(ContainerModel type, NotificationFrequency frequency, OnDeleteNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnDeleteNodePolicy.class, policy);
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
