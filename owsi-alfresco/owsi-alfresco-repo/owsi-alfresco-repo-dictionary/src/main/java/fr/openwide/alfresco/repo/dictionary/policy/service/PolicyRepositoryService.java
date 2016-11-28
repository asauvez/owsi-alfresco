package fr.openwide.alfresco.repo.dictionary.policy.service;

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
import org.alfresco.repo.policy.ClassPolicy;

import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;

public interface PolicyRepositoryService {

	<T extends ClassPolicy> void bindClassBehaviour(ContainerModel type, NotificationFrequency frequency, Class<T> eventType, T policy);
	<T extends AssociationPolicy> void bindAssociationBehaviour(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency, Class<T> eventType, T policy);

	void onUpdateNode(ContainerModel type, NotificationFrequency frequency, OnUpdateNodePolicy policy);
	void onUpdateProperties(ContainerModel type, NotificationFrequency frequency, OnUpdatePropertiesPolicy policy);

	void beforeAddAspect(ContainerModel type, NotificationFrequency frequency, BeforeAddAspectPolicy policy);
	void onAddAspect(ContainerModel type, NotificationFrequency frequency, OnAddAspectPolicy policy);
	
	void onRemoveAspect(ContainerModel type, NotificationFrequency frequency, OnRemoveAspectPolicy policy);
	
	void beforeCreateNode(ContainerModel type, NotificationFrequency frequency, BeforeCreateNodePolicy policy);
	void onCreateNode(ContainerModel type, NotificationFrequency frequency, OnCreateNodePolicy policy);
	
	void beforeMoveNode(ContainerModel type, NotificationFrequency frequency, BeforeMoveNodePolicy policy);
	void onMoveNode(ContainerModel type, NotificationFrequency frequency, OnMoveNodePolicy policy);
	
	void beforeDeleteNode(ContainerModel type, NotificationFrequency frequency, BeforeDeleteNodePolicy policy);
	void onDeleteNode(ContainerModel type, NotificationFrequency frequency, OnDeleteNodePolicy policy);
	
	void onCreateChildAssociation(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency, OnCreateChildAssociationPolicy policy);
	void onDeleteChildAssociation(ContainerModel type, ChildAssociationModel association, NotificationFrequency frequency, OnDeleteChildAssociationPolicy policy);

	<T> T disableBehaviour(ContainerModel type, Callable<T> callable);
	
//    public interface BeforeUpdateNodePolicy extends ClassPolicy
//    public interface BeforeArchiveNodePolicy extends ClassPolicy
//    public interface BeforeRemoveAspectPolicy extends ClassPolicy
//    public interface OnRestoreNodePolicy extends ClassPolicy
//    public interface BeforeSetNodeTypePolicy extends ClassPolicy
//    public interface OnSetNodeTypePolicy extends ClassPolicy

//  public interface BeforeDeleteChildAssociationPolicy extends AssociationPolicy
//  public interface OnCreateAssociationPolicy extends AssociationPolicy
//  public interface BeforeDeleteAssociationPolicy extends AssociationPolicy
//  public interface OnDeleteAssociationPolicy extends AssociationPolicy
}
