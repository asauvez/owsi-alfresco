package fr.openwide.alfresco.repo.dictionary.policy.service.impl;

import java.util.concurrent.Callable;

import org.alfresco.repo.node.NodeServicePolicies.BeforeAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.BeforeMoveNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteChildAssociationPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnMoveNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.AssociationPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repo.dictionary.policy.service.PolicyRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class PolicyRepositoryServiceImpl implements PolicyRepositoryService {

	private PolicyComponent policyComponent;
	private BehaviourFilter policyFilter;

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
	public void beforeAddAspect(ContainerModel type, NotificationFrequency frequency, BeforeAddAspectPolicy policy) {
		bindClassBehaviour(type, frequency, BeforeAddAspectPolicy.class, policy);
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
	public void onUpdateNode(ContainerModel type, NotificationFrequency frequency, OnUpdateNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnUpdateNodePolicy.class, policy);
	}
	@Override
	public void onUpdateProperties(ContainerModel type, NotificationFrequency frequency, OnUpdatePropertiesPolicy policy) {
		bindClassBehaviour(type, frequency, OnUpdatePropertiesPolicy.class, policy);
	}
	
	@Override
	public void beforeCreateNode(ContainerModel type, NotificationFrequency frequency, BeforeCreateNodePolicy policy) {
		bindClassBehaviour(type, frequency, BeforeCreateNodePolicy.class, policy);
	}
	@Override
	public void onCreateNode(ContainerModel type, NotificationFrequency frequency, OnCreateNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnCreateNodePolicy.class, policy);
	}
	@Override
	public void beforeMoveNode(ContainerModel type, NotificationFrequency frequency, BeforeMoveNodePolicy policy) {
		bindClassBehaviour(type, frequency, BeforeMoveNodePolicy.class, policy);
	}
	@Override
	public void onMoveNode(ContainerModel type, NotificationFrequency frequency, OnMoveNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnMoveNodePolicy.class, policy);
	}
	@Override
	public void beforeDeleteNode(ContainerModel type, NotificationFrequency frequency, BeforeDeleteNodePolicy policy) {
		bindClassBehaviour(type, frequency, BeforeDeleteNodePolicy.class, policy);
	}
	@Override
	public void onDeleteNode(ContainerModel type, NotificationFrequency frequency, OnDeleteNodePolicy policy) {
		bindClassBehaviour(type, frequency, OnDeleteNodePolicy.class, policy);
	}
	@Override
	public void onCreateChildAssociation(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency, OnCreateChildAssociationPolicy policy) {
		bindAssociationBehaviour(type, association, frequency, OnCreateChildAssociationPolicy.class, policy);
	}
	@Override
	public void onDeleteChildAssociation(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency, OnDeleteChildAssociationPolicy policy) {
		bindAssociationBehaviour(type, association, frequency, OnDeleteChildAssociationPolicy.class, policy);
	}
	
	@Override
	public <T> T disableBehaviour(ContainerModel type, Callable<T> callable) {
		QName typeQName = conversionService.getRequired(type.getNameReference());
		policyFilter.disableBehaviour(typeQName);
		try {
			return callable.call();
		} catch (Exception e) {
			throw (e instanceof RuntimeException) ? (RuntimeException) e : new IllegalStateException(e);
		} finally {
			policyFilter.enableBehaviour(typeQName);
		}
	}
	@Override
	public void disableBehaviour(ContainerModel type, Runnable runnable) {
		disableBehaviour(type, () -> null);
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	public void setPolicyFilter(BehaviourFilter policyFilter) {
		this.policyFilter = policyFilter;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
