package fr.openwide.alfresco.repo.dictionary.node.service.impl;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.ClassPolicy;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.node.service.NodeRemoteService;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.node.service.impl.NodeModelServiceImpl;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repository.core.node.service.impl.NodeRemoteServiceImpl;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;

public class NodeModelRepositoryServiceImpl 
	extends NodeModelServiceImpl
	implements NodeModelRepositoryService {

	private NodeService nodeService;
	private CopyService copyService;
	private PolicyComponent policyComponent;

	private ConversionService conversionService;
	
	public NodeModelRepositoryServiceImpl(NodeRemoteService nodeService) {
		super(nodeService);
	}
	
	@Override
	public boolean exists(NodeReference nodeReference) {
		return nodeService.exists(conversionService.getRequired(nodeReference));
	}
	
	@Override
	public void moveNode(NodeReference nodeReference, NodeReference newParentRef) {
		String nodeName = getProperty(nodeReference, CmModel.object.name);
		nodeService.moveNode(conversionService.getRequired(nodeReference), 
				conversionService.getRequired(newParentRef), 
				conversionService.getRequired(CmModel.folder.contains.getNameReference()), 
				NodeRemoteServiceImpl.createAssociationName(nodeName));
	}

	@Override
	public void copy(NodeReference nodeReference, NodeReference newParentRef) {
		copyService.copy(conversionService.getRequired(nodeReference), 
				conversionService.getRequired(newParentRef));
	}

	@Override
	public NameReference getType(NodeReference nodeReference) {
		return conversionService.get(nodeService.getType(conversionService.getRequired(nodeReference)));
	}

	@Override
	public void setType(NodeReference nodeReference, NameReference type) {
		nodeService.setType(conversionService.getRequired(nodeReference), conversionService.getRequired(type));
	}
	@Override
	public void setType(NodeReference nodeReference, TypeModel type) {
		setType(nodeReference, type.getNameReference());
	}

	@Override
	public Set<NameReference> getAspects(NodeReference nodeReference) {
		Set<QName> aspects = nodeService.getAspects(conversionService.getRequired(nodeReference));
		Set<NameReference> nameReferences = new LinkedHashSet<>();
		for (QName aspect : aspects) {
			nameReferences.add(conversionService.get(aspect));
		}
		return nameReferences;
	}

	@Override
	public boolean hasAspect(NodeReference nodeReference, NameReference aspect) {
		return nodeService.hasAspect(conversionService.getRequired(nodeReference), conversionService.getRequired(aspect));
	}
	@Override
	public boolean hasAspect(NodeReference nodeReference, AspectModel aspect) {
		return hasAspect(nodeReference, aspect.getNameReference());
	}

	@Override
	public void addAspect(NodeReference nodeReference, AspectModel aspect) {
		addAspect(nodeReference, aspect, new BusinessNode());
	}
	@Override
	public void addAspect(NodeReference nodeReference, NameReference aspect) {
		addAspect(nodeReference, aspect, new BusinessNode());
	}
	@Override
	public void addAspect(NodeReference nodeReference, NameReference aspect, BusinessNode node) {
		nodeService.addAspect(conversionService.getRequired(nodeReference), 
				conversionService.getRequired(aspect), 
				conversionService.getForRepository(node.getRepositoryNode().getProperties()));
	}
	@Override
	public void addAspect(NodeReference nodeReference, AspectModel aspect, BusinessNode node) {
		addAspect(nodeReference, aspect.getNameReference(), node);
	}
	
	@Override
	public void removeAspect(NodeReference nodeReference, NameReference aspect) {
		nodeService.removeAspect(conversionService.getRequired(nodeReference), 
				conversionService.getRequired(aspect));
	}
	@Override
	public void removeAspect(NodeReference nodeReference, AspectModel aspect) {
		removeAspect(nodeReference, aspect.getNameReference());
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(NodeReference nodeReference, NameReference property) {
		return (C) nodeService.getProperty(
				conversionService.getRequired(nodeReference),
				conversionService.getRequired(property));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(NodeReference nodeReference, SinglePropertyModel<C> property) {
		return (C) getProperty(nodeReference, property.getNameReference());
	}
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(NodeReference nodeReference, MultiPropertyModel<C> property) {
		return (List<C>) getProperty(nodeReference, property.getNameReference());
	}

	@Override
	public <C extends Serializable> void setProperty(NodeReference nodeReference, SinglePropertyModel<C> property, C value) {
		setProperty(nodeReference, property.getNameReference(), value);
	}
	@Override
	public <C extends Serializable> void setProperty(NodeReference nodeReference, MultiPropertyModel<C> property, List<C> value) {
		setProperty(nodeReference, property.getNameReference(), (Serializable) value);
	}

	@Override
	public <C extends Serializable> void setProperty(NodeReference nodeReference, NameReference property, C value) {
		nodeService.setProperty(
				conversionService.getRequired(nodeReference),
				conversionService.getRequired(property),
				conversionService.getForRepository(value));
	}

	@Override
	public Optional<NodeReference> getChildByName(NodeReference nodeReference, String childName) {
		return getChildByName(nodeReference, childName, CmModel.folder.contains);
	}
	@Override
	public Optional<NodeReference> getChildByName(NodeReference nodeReference, String childName, ChildAssociationModel associationType) {
		return getChildByName(nodeReference, childName, associationType.getNameReference());
	}

	@Override
	public Optional<NodeReference> getChildByName(NodeReference nodeReference, String childName, NameReference associationType) {
		NodeRef subNodeRef = nodeService.getChildByName(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(associationType), 
				QName.createValidLocalName(childName));
		NodeReference subnodeReference = (subNodeRef != null) ? conversionService.get(subNodeRef) : null;
		return Optional.fromNullable(subnodeReference);
	}
	
	@Override
	public void addChild(NodeReference parentRef, NodeReference childRef) {
		addChild(parentRef, childRef, CmModel.folder.contains);
	}
	@Override
	public void addChild(NodeReference parentRef, NodeReference childRef, ChildAssociationModel assocType) {
		addChild(parentRef, childRef, assocType.getNameReference());
	}
	@Override
	public void addChild(NodeReference parentRef, NodeReference childRef, NameReference assocType) {
		String childName = getProperty(childRef, CmModel.object.name);
		nodeService.addChild(conversionService.getRequired(parentRef), 
				conversionService.getRequired(childRef), 
				conversionService.getRequired(assocType), 
				NodeRemoteServiceImpl.createAssociationName(childName));
	}

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
	

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setCopyService(CopyService copyService) {
		this.copyService = copyService;
	}
	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}

}
