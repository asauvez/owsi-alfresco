package fr.openwide.alfresco.repo.dictionary.node.service;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.association.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;

public interface NodeModelRepositoryService extends NodeModelService {

	boolean exists(NodeReference nodeReference);
	void moveNode(NodeReference nodeReference, NodeReference newParentRef);
	NodeReference copy(NodeReference nodeReference, NodeReference newParentRef, Optional<String> newName);

	void addChild(NodeReference parentRef, NodeReference childRef);
	void addChild(NodeReference parentRef, NodeReference childRef, ChildAssociationModel assocType);
	void addChild(NodeReference parentRef, NodeReference childRef, NameReference assocType);

	void removeChild(NodeReference parentRef, NodeReference childRef);
	void removeChild(NodeReference parentRef, NodeReference childRef, ChildAssociationModel assocType);
	void removeChild(NodeReference parentRef, NodeReference childRef, NameReference assocType);
	void unlinkSecondaryParents(NodeReference nodeReference, ChildAssociationModel childAssociationModel);

	boolean isType(NodeReference nodeReference, TypeModel typeModel);
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
	
	void deletePermanently(NodeReference nodeReference);
	
	<C extends Serializable> C getProperty(NodeReference nodeReference, NameReference property);
	<C extends Serializable> C getProperty(NodeReference nodeReference, SinglePropertyModel<C> property);
	<E extends Enum<E>> E getProperty(NodeReference nodeReference, EnumTextPropertyModel<E> property);
	<C extends Serializable> List<C> getProperty(NodeReference nodeReference, MultiPropertyModel<C> property);
	
	<C extends Serializable> void setProperty(NodeReference nodeReference, SinglePropertyModel<C> property, C value);
	<E extends Enum<E>> void setProperty(NodeReference nodeReference, EnumTextPropertyModel<E> property, E value);
	<C extends Serializable> void setProperty(NodeReference nodeReference, MultiPropertyModel<C> property, List<C> value);
	<C extends Serializable> void setProperty(NodeReference nodeReference, NameReference property, C value);

	Optional<NodeReference> getPrimaryParent(NodeReference nodeReference);
	List<NodeReference> getParentAssocs(NodeReference nodeReference);
	
	Optional<NodeReference> getChildAssocs(NodeReference nodeReference, ChildAssociationModel associationType, NameReference assocName);
	List<NodeReference> getChildrenAssocs(NodeReference nodeReference, ChildAssociationModel associationType);
	List<NodeReference> getChildrenAssocsContains(NodeReference nodeReference);

	Optional<NodeReference> getChildByName(NodeReference nodeReference, String childName);
	Optional<NodeReference> getChildByName(NodeReference nodeReference, String childName, ChildAssociationModel associationType);
	Optional<NodeReference> getChildByName(NodeReference nodeReference, String childName, NameReference associationType);

	void createAssociation(NodeReference sourceRef, NodeReference targetRef, AssociationModel assocType);
	void createAssociation(NodeReference sourceRef, NodeReference targetRef, NameReference assocType);
	void removeAssociation(NodeReference sourceRef, NodeReference targetRef, AssociationModel assocType);
	void removeAssociation(NodeReference sourceRef, NodeReference targetRef, NameReference assocType);
	
	NodeReference getCompanyHome();
	NodeReference getDataDictionary();
	Optional<NodeReference> getUserHome();
	
	Optional<NodeReference> getByNamedPath(String ... names);
	String getPath(NodeReference nodeReference);
	
	<C extends Serializable> void removeProperty(NodeReference nodeReference, SinglePropertyModel<C> property);
}
