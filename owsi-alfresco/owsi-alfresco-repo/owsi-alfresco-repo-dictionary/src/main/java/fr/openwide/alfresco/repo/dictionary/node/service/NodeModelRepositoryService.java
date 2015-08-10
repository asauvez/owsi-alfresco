package fr.openwide.alfresco.repo.dictionary.node.service;

import java.io.Serializable;
import java.util.Set;

import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;

public interface NodeModelRepositoryService extends NodeModelService {

	boolean exists(NodeReference nodeReference);
	void moveNode(NodeReference nodeReference, NodeReference newParentRef);

	NameReference getType(NodeReference nodeReference);
	void setType(NodeReference nodeReference, NameReference type);
	void setType(NodeReference nodeReference, TypeModel type);
	
	Set<NameReference> getAspects(NodeReference nodeReference);
	boolean hasAspect(NodeReference nodeReference, NameReference aspect);
	boolean hasAspect(NodeReference nodeReference, AspectModel aspect);
	
	void addAspect(NodeReference nodeReference, NameReference aspect);
	void addAspect(NodeReference nodeReference, AspectModel aspect);
	void addAspect(NodeReference nodeReference, NameReference aspect, BusinessNode node);
	void addAspect(NodeReference nodeReference, AspectModel aspect, BusinessNode node);
	
	void removeAspect(NodeReference nodeReference, NameReference aspect);
	void removeAspect(NodeReference nodeReference, AspectModel aspect);
	
	<C extends Serializable> C getProperty(NodeReference nodeReference, NameReference property);
	<C extends Serializable> C getProperty(NodeReference nodeReference, PropertyModel<C> property);
	
	<C extends Serializable> void setProperty(NodeReference nodeReference, PropertyModel<C> property, C value);
	<C extends Serializable> void setProperty(NodeReference nodeReference, NameReference property, C value);

	Optional<NodeReference> getChildByName(NodeReference nodeReference, String childName);

	void bindClassBehaviour(ContainerModel type, ClassPolicy policy, NotificationFrequency frequency);
}
