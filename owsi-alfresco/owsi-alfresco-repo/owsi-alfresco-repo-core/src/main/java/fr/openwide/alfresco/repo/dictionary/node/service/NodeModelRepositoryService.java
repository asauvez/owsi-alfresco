package fr.openwide.alfresco.repo.dictionary.node.service;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.association.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.bean.NodeBean;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public interface NodeModelRepositoryService {

	NodeRef createNode(NodeRef parentRef, TypeModel type, String name) throws FileExistsException;
	NodeRef createNode(NodeRef parentRef, TypeModel type, String name, Map<QName, Serializable> properties) throws FileExistsException;
	NodeRef createNode(NodeRef parentRef, TypeModel type, String name, NodeBean properties) throws FileExistsException;
	NodeRef createFolder(NodeRef parentRef, String folderName) throws FileExistsException;
	NodeRef getOrCreateFolder(NodeRef parentRef, String folderName) throws FileExistsException;
	
	boolean exists(NodeRef nodeRef);
	void moveNode(NodeRef nodeRef, NodeRef newParentRef);
	NodeRef copy(NodeRef nodeRef, NodeRef newParentRef, Optional<String> newName);

	void addChild(NodeRef parentRef, NodeRef childRef);
	void addChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType);
	void addChild(NodeRef parentRef, NodeRef childRef, QName assocType);

	void removeChild(NodeRef parentRef, NodeRef childRef);
	void removeChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType);
	void removeChild(NodeRef parentRef, NodeRef childRef, QName assocType);
	void unlinkSecondaryParents(NodeRef nodeRef, ChildAssociationModel childAssociationModel);

	boolean isFolder(NodeRef nodeRef);
	boolean isType(NodeRef nodeRef, TypeModel typeModel);
	QName getType(NodeRef nodeRef);
	void setType(NodeRef nodeRef, QName type);
	void setType(NodeRef nodeRef, TypeModel type);
	
	Set<QName> getAspects(NodeRef nodeRef);
	boolean hasAspect(NodeRef nodeRef, QName aspect);
	boolean hasAspect(NodeRef nodeRef, AspectModel aspect);
	
	void addAspect(NodeRef nodeRef, QName aspect);
	void addAspect(NodeRef nodeRef, QName aspect, Map<QName, Serializable> properties);
	void addAspect(NodeRef nodeRef, AspectModel aspect);
	void addAspect(NodeRef nodeRef, AspectModel aspect, Map<QName, Serializable> properties);
	void addAspect(NodeRef nodeRef, AspectModel aspect, NodeBean properties);
	
	void removeAspect(NodeRef nodeRef, QName aspect);
	void removeAspect(NodeRef nodeRef, AspectModel aspect);
	
	void deleteNode(NodeRef nodeRef);
	void deleteNodePermanently(NodeRef nodeRef);
	
	Map<QName, Serializable> getProperties(NodeRef nodeRef);
	<B extends NodeBean> B getProperties(NodeRef nodeRef, B bean);
	void setProperties(NodeRef nodeRef, Map<QName, Serializable> properties);
	void setProperties(NodeRef nodeRef, NodeBean bean);

	<C extends Serializable> C getProperty(NodeRef nodeRef, QName property);
	<C extends Serializable> C getProperty(NodeRef nodeRef, SinglePropertyModel<C> property);
	<E extends Enum<E>> E getProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property);
	<C extends Serializable> List<C> getProperty(NodeRef nodeRef, MultiPropertyModel<C> property);
	
	<C extends Serializable> void setProperty(NodeRef nodeRef, SinglePropertyModel<C> property, C value);
	<E extends Enum<E>> void setProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property, E value);
	<C extends Serializable> void setProperty(NodeRef nodeRef, MultiPropertyModel<C> property, List<C> value);
	<C extends Serializable> void setProperty(NodeRef nodeRef, QName property, C value);
	<C extends Serializable> void copyProperty(NodeRef source, NodeRef target, PropertyModel<C> property);

	<C extends Serializable> C getProperty(Map<QName, Serializable> values, SinglePropertyModel<C> property);
	<C extends Serializable> List<C> getProperty(Map<QName, Serializable> values, MultiPropertyModel<C> property);
	<E extends Enum<E>> E getProperty(Map<QName, Serializable> values, EnumTextPropertyModel<E> property);

	<C extends Serializable> void setProperty(Map<QName, Serializable> values, SinglePropertyModel<C> property, C value);
	<C extends Serializable> void setProperty(Map<QName, Serializable> values, MultiPropertyModel<C> property, List<C> value);
	<E extends Enum<E>> void setProperty(Map<QName, Serializable> values, EnumTextPropertyModel<E> property, E value);

	Optional<NodeRef> getPrimaryParent(NodeRef nodeRef);
	List<NodeRef> getParentAssocs(NodeRef nodeRef);
	
	Optional<NodeRef> getChildAssocs(NodeRef nodeRef, ChildAssociationModel associationType, QName assocName);
	List<NodeRef> getChildrenAssocs(NodeRef nodeRef, ChildAssociationModel associationType);
	List<NodeRef> getChildrenAssocsContains(NodeRef nodeRef);
	void visitAllChildrenContains(NodeRef nodeRef, Consumer<NodeRef> visitor);

	Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName);
	Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, ChildAssociationModel associationType);
	Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, QName associationType);


	@Deprecated
	/**
	 * Utiliser le UniqueNameRepositoryService
	 */
	String getUniqueChildName(NodeRef folder, String originalName);
	@Deprecated
	/**
	 * Utiliser le UniqueNameRepositoryService
	 */
	String getUniqueChildName(NodeRef folder, NodeRef document);
	
	void createAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType);
	void createAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocType);
	void removeAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType);
	void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocType);

	List<NodeRef> getTargetAssocs(NodeRef nodeRef, ManyToManyAssociationModel assoc);
	List<NodeRef> getSourceAssocs(NodeRef nodeRef, ManyToManyAssociationModel assoc);

	Optional<NodeRef> getTargetAssocs(NodeRef nodeRef, ManyToOneAssociationModel assoc);
	List<NodeRef> getSourceAssocs(NodeRef nodeRef, ManyToOneAssociationModel assoc);

	List<NodeRef> getTargetAssocs(NodeRef nodeRef, OneToManyAssociationModel assoc);
	Optional<NodeRef> getSourceAssocs(NodeRef nodeRef, OneToManyAssociationModel assoc);

	Optional<NodeRef> getTargetAssocs(NodeRef nodeRef, OneToOneAssociationModel assoc);
	Optional<NodeRef> getSourceAssocs(NodeRef nodeRef, OneToOneAssociationModel assoc);

	
	NodeRef getCompanyHome();
	NodeRef getSharedHome();
	NodeRef getDataDictionary();
	Optional<NodeRef> getUserHome();
	
	Optional<NodeRef> getByNamedPath(String ... names);
	String getPath(NodeRef nodeRef);
	
	<C extends Serializable> void removeProperty(NodeRef nodeRef, SinglePropertyModel<C> property);
}
