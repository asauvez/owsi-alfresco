package fr.openwide.alfresco.repo.dictionary.node.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.alfresco.service.cmr.repository.NodeRef;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.association.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public interface NodeModelRepositoryService {

	NodeRef createNode(NodeRef parentRef, TypeModel type, String name) throws DuplicateChildNodeNameRemoteException;
	NodeRef createFolder(NodeRef parentRef, String folderName) throws DuplicateChildNodeNameRemoteException;
	
	boolean exists(NodeRef nodeRef);
	void moveNode(NodeRef nodeRef, NodeRef newParentRef);
	NodeRef copy(NodeRef nodeRef, NodeRef newParentRef, Optional<String> newName);

	void addChild(NodeRef parentRef, NodeRef childRef);
	void addChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType);
	void addChild(NodeRef parentRef, NodeRef childRef, NameReference assocType);

	void removeChild(NodeRef parentRef, NodeRef childRef);
	void removeChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType);
	void removeChild(NodeRef parentRef, NodeRef childRef, NameReference assocType);
	void unlinkSecondaryParents(NodeRef nodeRef, ChildAssociationModel childAssociationModel);

	boolean isType(NodeRef nodeRef, TypeModel typeModel);
	NameReference getType(NodeRef nodeRef);
	void setType(NodeRef nodeRef, NameReference type);
	void setType(NodeRef nodeRef, TypeModel type);
	
	Set<NameReference> getAspects(NodeRef nodeRef);
	boolean hasAspect(NodeRef nodeRef, NameReference aspect);
	boolean hasAspect(NodeRef nodeRef, AspectModel aspect);
	
	void addAspect(NodeRef nodeRef, NameReference aspect);
	void addAspect(NodeRef nodeRef, AspectModel aspect);
	void addAspect(NodeRef nodeRef, NameReference aspect, BusinessNode node);
	void addAspect(NodeRef nodeRef, AspectModel aspect, BusinessNode node);
	
	void removeAspect(NodeRef nodeRef, NameReference aspect);
	void removeAspect(NodeRef nodeRef, AspectModel aspect);
	
	void deleteNode(NodeRef nodeRef);
	void deleteNodePermanently(NodeRef nodeRef);
	
	Map<NameReference, Serializable> getProperties(NodeRef nodeRef);
	<C extends Serializable> C getProperty(NodeRef nodeRef, NameReference property);
	<C extends Serializable> C getProperty(NodeRef nodeRef, SinglePropertyModel<C> property);
	NodeRef getProperty(NodeRef nodeRef, NodeReferencePropertyModel property);
	<E extends Enum<E>> E getProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property);
	<C extends Serializable> List<C> getProperty(NodeRef nodeRef, MultiPropertyModel<C> property);
	List<NodeRef> getProperty(NodeRef nodeRef, MultiNodeReferencePropertyModel property);
	
	<C extends Serializable> void setProperty(NodeRef nodeRef, SinglePropertyModel<C> property, C value);
	void setProperty(NodeRef nodeRef, NodeReferencePropertyModel property, NodeRef value);
	<E extends Enum<E>> void setProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property, E value);
	<C extends Serializable> void setProperty(NodeRef nodeRef, MultiPropertyModel<C> property, List<C> value);
	<C extends Serializable> void setProperty(NodeRef nodeRef, NameReference property, C value);
	void setProperty(NodeRef nodeRef, MultiNodeReferencePropertyModel property, List<NodeRef> value);

	Optional<NodeRef> getPrimaryParent(NodeRef nodeRef);
	List<NodeRef> getParentAssocs(NodeRef nodeRef);
	
	Optional<NodeRef> getChildAssocs(NodeRef nodeRef, ChildAssociationModel associationType, NameReference assocName);
	List<NodeRef> getChildrenAssocs(NodeRef nodeRef, ChildAssociationModel associationType);
	List<NodeRef> getChildrenAssocsContains(NodeRef nodeRef);

	Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName);
	Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, ChildAssociationModel associationType);
	Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, NameReference associationType);

	String getUniqueChildName(NodeRef folder, String originalName);
	String getUniqueChildName(NodeRef folder, NodeRef document);
	
	void createAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType);
	void createAssociation(NodeRef sourceRef, NodeRef targetRef, NameReference assocType);
	void removeAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType);
	void removeAssociation(NodeRef sourceRef, NodeRef targetRef, NameReference assocType);

	List<NodeRef> getTargetAssocs(NodeRef nodeRef, ManyToManyAssociationModel assoc);
	List<NodeRef> getSourceAssocs(NodeRef nodeRef, ManyToManyAssociationModel assoc);

	Optional<NodeRef> getTargetAssocs(NodeRef nodeRef, ManyToOneAssociationModel assoc);
	List<NodeRef> getSourceAssocs(NodeRef nodeRef, ManyToOneAssociationModel assoc);

	List<NodeRef> getTargetAssocs(NodeRef nodeRef, OneToManyAssociationModel assoc);
	Optional<NodeRef> getSourceAssocs(NodeRef nodeRef, OneToManyAssociationModel assoc);

	Optional<NodeRef> getTargetAssocs(NodeRef nodeRef, OneToOneAssociationModel assoc);
	Optional<NodeRef> getSourceAssocs(NodeRef nodeRef, OneToOneAssociationModel assoc);

	
	NodeRef getCompanyHome();
	NodeRef getDataDictionary();
	Optional<NodeRef> getUserHome();
	
	Optional<NodeRef> getByNamedPath(String ... names);
	String getPath(NodeRef nodeRef);
	
	<C extends Serializable> void removeProperty(NodeRef nodeRef, SinglePropertyModel<C> property);
}
