package fr.openwide.alfresco.repo.dictionary.policy.service;

import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnDeleteNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.ClassPolicy;

import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public interface PolicyRepositoryService {

	<T extends ClassPolicy> void bindClassBehaviour(ContainerModel type, NotificationFrequency frequency, Class<T> eventType, T policy);

	void onUpdateProperties(ContainerModel type, NotificationFrequency frequency, OnUpdatePropertiesPolicy policy);
	void onAddAspect(ContainerModel type, NotificationFrequency frequency, OnAddAspectPolicy policy);
	void onDeleteNodePolicy(ContainerModel type, NotificationFrequency frequency, OnDeleteNodePolicy policy);

}
