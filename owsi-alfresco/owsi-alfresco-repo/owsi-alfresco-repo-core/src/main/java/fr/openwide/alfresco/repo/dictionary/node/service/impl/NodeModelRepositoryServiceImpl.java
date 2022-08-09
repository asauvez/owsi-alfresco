package fr.openwide.alfresco.repo.dictionary.node.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.dictionary.NamespaceDAO;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileNotFoundException;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

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
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;

public class NodeModelRepositoryServiceImpl implements NodeModelRepositoryService {

	@Autowired private NodeService nodeService;
	@Autowired private FileFolderService fileFolderService;
	@Autowired private NamespaceDAO namespaceDAO;
	private Repository repositoryHelper;

	private String dataDictionaryChildName;
	private SimpleCache<String, NodeRef> singletonCache; // eg. for dataDictionaryNodeRef
	private final String KEY_DATADICTIONARY_NODEREF = "owsi.key.datadictionary.noderef";
	
	@Override
	public NodeRef createNode(NodeRef parentRef, TypeModel type, String name) throws FileExistsException {
		return createNode(parentRef, type, name, new HashMap<>());
	}
	@Override
	public NodeRef createNode(NodeRef parentRef, TypeModel type, String name, Map<QName, Serializable> properties) throws FileExistsException {
		setProperty(properties, CmModel.cmobject.name, name);
		
		return nodeService.createNode(parentRef, 
				ContentModel.ASSOC_CONTAINS, 
				createAssociationName(name), 
				type.getQName(),
				properties).getChildRef();
	}
	@Override
	public NodeRef createNode(NodeRef parentRef, TypeModel type, String name, NodeBean bean) throws FileExistsException {
		Map<QName, Serializable> properties = new HashMap<>();
		for (Entry<QName, Serializable> entry : bean.getProperties().entrySet()) {
			properties.put(entry.getKey(), entry.getValue());
		}
		return createNode(parentRef, type, name, properties);
	}
	
	
	@Override
	public NodeRef createFolder(NodeRef parentRef, String folderName) throws FileExistsException {
		return fileFolderService.create(parentRef, folderName, ContentModel.TYPE_FOLDER).getNodeRef();
	}
	@Override
	public NodeRef getOrCreateFolder(NodeRef parentRef, String folderName)
			throws FileExistsException {
		NodeRef folderRef = fileFolderService.searchSimple(parentRef, folderName);
		if (folderRef == null) {
			folderRef = createFolder(parentRef, folderName);
		}
		return folderRef;
	}
	
	@Override
	public boolean exists(NodeRef nodeRef) {
		return nodeService.exists(nodeRef);
	}
	
	@Override
	public void moveNode(NodeRef nodeRef, NodeRef newParentRef) {
		try {
			fileFolderService.move(nodeRef, newParentRef, null);
		} catch (FileExistsException | FileNotFoundException e) {
			throw new IllegalStateException("Exception during move node", e);
		}
	}

	@Override
	public NodeRef copy(NodeRef nodeRef, NodeRef newParentRef, Optional<String> newName) {
		try {
			return fileFolderService.copy(nodeRef, newParentRef, newName.orElse(null)).getNodeRef();
		} catch (FileExistsException | FileNotFoundException e) {
			throw new IllegalStateException("Exception during copy node", e);
		}
	}

	@Override
	public boolean isFolder(NodeRef nodeRef) {
		return fileFolderService.getFileInfo(nodeRef).isFolder();
	}
	
	@Override
	public boolean isType(NodeRef nodeRef, TypeModel typeModel) {
		return typeModel.getQName().equals(getType(nodeRef));
	}
	@Override
	public QName getType(NodeRef nodeRef) {
		return nodeService.getType(nodeRef);
	}

	@Override
	public void setType(NodeRef nodeRef, QName type) {
		nodeService.setType(nodeRef, type);
	}
	@Override
	public void setType(NodeRef nodeRef, TypeModel type) {
		setType(nodeRef, type.getQName());
	}

	@Override
	public Set<QName> getAspects(NodeRef nodeRef) {
		return nodeService.getAspects(nodeRef);
	}

	@Override
	public boolean hasAspect(NodeRef nodeRef, QName aspect) {
		return nodeService.hasAspect(nodeRef, aspect);
	}
	@Override
	public boolean hasAspect(NodeRef nodeRef, AspectModel aspect) {
		return hasAspect(nodeRef, aspect.getQName());
	}

	@Override
	public void addAspect(NodeRef nodeRef, QName aspect) {
		addAspect(nodeRef, aspect, null);
	}
	@Override
	public void addAspect(NodeRef nodeRef, QName aspect, Map<QName, Serializable> properties) {
		nodeService.addAspect(nodeRef, 
				aspect, 
				properties);
	}
	@Override
	public void addAspect(NodeRef nodeRef, AspectModel aspect) {
		addAspect(nodeRef, aspect.getQName());
	}
	@Override
	public void addAspect(NodeRef nodeRef, AspectModel aspect, Map<QName, Serializable> properties) {
		addAspect(nodeRef, aspect.getQName(), properties);
	}
	@Override
	public void addAspect(NodeRef nodeRef, AspectModel aspect, NodeBean bean) {
		Map<QName, Serializable> properties = new HashMap<>();
		for (Entry<QName, Serializable> entry : bean.getProperties().entrySet()) {
			properties.put(entry.getKey(), entry.getValue());
		}
		addAspect(nodeRef, aspect.getQName(), properties);
	}
	
	@Override
	public void removeAspect(NodeRef nodeRef, QName aspect) {
		nodeService.removeAspect(nodeRef, aspect);
	}
	@Override
	public void removeAspect(NodeRef nodeRef, AspectModel aspect) {
		removeAspect(nodeRef, aspect.getQName());
	}

	@Override
	public void deleteNode(NodeRef nodeRef) {
		nodeService.deleteNode(nodeRef);
	}
	@Override
	public void deleteNodePermanently(NodeRef nodeRef) {
		addAspect(nodeRef, SysModel.temporary);
		deleteNode(nodeRef);
	}

	@Override
	public Map<QName, Serializable> getProperties(NodeRef nodeRef) {
		return nodeService.getProperties(nodeRef);
	}
	@Override
	public <B extends NodeBean> B getProperties(NodeRef nodeRef, B bean) {
		bean.getProperties().putAll(getProperties(nodeRef));
		return bean;
	}
	@Override
	public void setProperties(NodeRef nodeRef, Map<QName, Serializable> props) {
		nodeService.setProperties(nodeRef, props);
	}
	@Override
	public void setProperties(NodeRef nodeRef, NodeBean bean) {
		setProperties(nodeRef, bean.getProperties());
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(NodeRef nodeRef, QName property) {
		return (C) nodeService.getProperty(nodeRef, property);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(NodeRef nodeRef, SinglePropertyModel<C> property) {
		return (C) getProperty(nodeRef, property.getQName());
	}
	@Override
	public <E extends Enum<E>> E getProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property) {
		return NodeBean.textToEnum(property, (String) getProperty(nodeRef, property.getQName()));
	}
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(NodeRef nodeRef, MultiPropertyModel<C> property) {
		return (List<C>) getProperty(nodeRef, property.getQName());
	}
	@Override
	public <C extends Serializable> void setProperty(NodeRef nodeRef, SinglePropertyModel<C> property, C value) {
		setProperty(nodeRef, property.getQName(), value);
	}
	@Override
	public <E extends Enum<E>> void setProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property, E value) {
		String code = NodeBean.enumToText(value);
		setProperty(nodeRef, property.getQName(), code);
	}
	@Override
	public <C extends Serializable> void setProperty(NodeRef nodeRef, MultiPropertyModel<C> property, List<C> value) {
		setProperty(nodeRef, property.getQName(), (Serializable) value);
	}
	@Override
	public <C extends Serializable> void copyProperty(NodeRef source, NodeRef target, PropertyModel<C> property) {
		C value = getProperty(source, property.getQName());
		setProperty(target, property.getQName(), value);
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(Map<QName, Serializable> values, SinglePropertyModel<C> property) {
		return (C) values.get(property.getQName());
	}
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(Map<QName, Serializable> values, MultiPropertyModel<C> property) {
		return (List<C>) values.get(property.getQName());
	}
	@Override
	public <E extends Enum<E>> E getProperty(Map<QName, Serializable> values, EnumTextPropertyModel<E> property) {
		return NodeBean.textToEnum(property, (String) values.get(property.getQName()));
	}
	
	@Override
	public <C extends Serializable> void setProperty(Map<QName, Serializable> values, SinglePropertyModel<C> property, C value) {
		values.put(property.getQName(), value);
	}
	@Override
	public <C extends Serializable> void setProperty(Map<QName, Serializable> values, MultiPropertyModel<C> property, List<C> value) {
		values.put(property.getQName(), (Serializable) value);
	}
	@Override
	public <E extends Enum<E>> void setProperty(Map<QName, Serializable> values, EnumTextPropertyModel<E> property, E value) {
		values.put(property.getQName(), NodeBean.enumToText(value));
	}

	

	@Override
	public <C extends Serializable> void setProperty(NodeRef nodeRef, QName property, C value) {
		nodeService.setProperty(nodeRef, property, value);
	}
	@Override
	public <C extends Serializable>  void removeProperty(NodeRef nodeRef, SinglePropertyModel<C> property) {
		nodeService.removeProperty(nodeRef, property.getQName());
	}

	@Override
	public Optional<NodeRef> getPrimaryParent(NodeRef nodeRef) {
		ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
		return (primaryParent != null && primaryParent.getParentRef() != null) 
				? Optional.of(primaryParent.getParentRef())
				: Optional.<NodeRef>empty();
	}

	@Override
	public List<NodeRef> getParentAssocs(NodeRef nodeRef) {
		List<ChildAssociationRef> children = nodeService.getParentAssocs(nodeRef);
		List<NodeRef> list = new ArrayList<>();
		for (ChildAssociationRef child : children) {
			list.add(child.getParentRef());
		}
		return list;
	}

	@Override
	public Optional<NodeRef> getChildAssocs(NodeRef nodeRef, ChildAssociationModel associationType, QName assocName) {
		List<ChildAssociationRef> children = nodeService.getChildAssocs(
				nodeRef, associationType.getQName(), assocName);
		return Optional.ofNullable((children.isEmpty()) 
				? null
				: children.get(0).getChildRef());
	}
	@Override
	public List<NodeRef> getChildrenAssocsContains(NodeRef nodeRef) {
		return getChildrenAssocs(nodeRef, CmModel.folder.contains);
	}
	@Override
	public List<NodeRef> getChildrenAssocs(NodeRef nodeRef, ChildAssociationModel associationType) {
		List<ChildAssociationRef> children = nodeService.getChildAssocs(
				nodeRef, 
				associationType.getQName(), 
				RegexQNamePattern.MATCH_ALL);
		return children.stream().map(child -> child.getChildRef())
				.collect(Collectors.toList());
	}
	@Override
	public Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName) {
		return Optional.ofNullable(fileFolderService.searchSimple(nodeRef, childName));
	}
	@Override
	public Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, ChildAssociationModel associationType) {
		return getChildByName(nodeRef, childName, associationType.getQName());
	}

	@Override
	public Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, QName associationType) {
		NodeRef subNodeRef = nodeService.getChildByName(
				nodeRef, 
				associationType, 
				QName.createValidLocalName(childName));
		NodeRef subnodeRef = (subNodeRef != null) ? subNodeRef : null;
		return Optional.ofNullable(subnodeRef);
	}
	
	@Override
	@Deprecated
	/**
	 * Utiliser le UniqueNameRepositoryService
	 */
	public String getUniqueChildName(NodeRef folder, String originalName) {
		String extension = FilenameUtils.getExtension(originalName);
		if (! extension.isEmpty()) {
			extension = "." + extension;
		}
		String baseName = FilenameUtils.removeExtension(originalName);
		String name = originalName;
		int index = 1;
		while (getChildByName(folder, name).isPresent()) {
			name = baseName + "-" + (index ++) + extension;
		}
		return name;
	}
	
	@Override
	@Deprecated
	/**
	 * Utiliser le UniqueNameRepositoryService
	 */
	public String getUniqueChildName(NodeRef folder, NodeRef document) {
		String originalName = getProperty(document, CmModel.cmobject.name);
		Optional<NodeRef> childByName = getChildByName(folder, originalName);
		if (! childByName.isPresent() || childByName.get().equals(document)) {
			return originalName;
		}
		return getUniqueChildName(folder, originalName);
	}
	
	@Override
	public void visitAllChildrenContains(NodeRef nodeRef, Consumer<NodeRef> visitor) {
		visitor.accept(nodeRef);
		
		for (NodeRef childRef : getChildrenAssocsContains(nodeRef)) {
			visitAllChildrenContains(childRef, visitor);
		}
	}
	
	@Override
	public void addChild(NodeRef parentRef, NodeRef childRef) {
		addChild(parentRef, childRef, CmModel.folder.contains);
	}
	@Override
	public void addChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType) {
		addChild(parentRef, childRef, assocType.getQName());
	}
	@Override
	public void addChild(NodeRef parentRef, NodeRef childRef, QName assocType) {
		String childName = getProperty(childRef, CmModel.cmobject.name);
		nodeService.addChild(parentRef, 
				childRef, 
				assocType, 
				createAssociationName(childName));
	}
	
	private QName createAssociationName(String nodeName) {
		return QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, QName.createValidLocalName(nodeName));
	}
	
	@Override
	public void removeChild(NodeRef parentRef, NodeRef childRef) {
		removeChild(parentRef, childRef, CmModel.folder.contains);
	}
	@Override
	public void removeChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType) {
		removeChild(parentRef, childRef, assocType.getQName());
	}
	@Override
	public void removeChild(NodeRef parentRef, NodeRef childRef, QName assocType) {
		String childName = getProperty(childRef, CmModel.cmobject.name);
		List<ChildAssociationRef> assocs = nodeService.getChildAssocs(
				parentRef, 
				assocType, 
				createAssociationName(childName));
		for (ChildAssociationRef assoc : assocs) {
			if (assoc.getChildRef().equals(childRef)) {
				nodeService.removeChildAssociation(assoc);
				return;
			}
		}
		throw new IllegalStateException("Can't find secondary child association " + parentRef + "/" + childRef);
	}
	@Override
	public void unlinkSecondaryParents(NodeRef nodeRef, ChildAssociationModel childAssociationModel) {
		List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(
				nodeRef, 
				childAssociationModel.getQName(), 
				RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef assoc : parentAssocs) {
			if (! assoc.isPrimary()) {
				nodeService.removeChildAssociation(assoc);
			}
		}
	}
	
	@Override
	public void createAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType) {
		createAssociation(sourceRef, targetRef, assocType.getQName());
	}
	@Override
	public void createAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocType) {
		nodeService.createAssociation(
				sourceRef, 
				targetRef, 
				assocType);
	}
	@Override
	public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType) {
		removeAssociation(sourceRef, targetRef, assocType.getQName());
	}
	@Override
	public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, QName assocType) {
		nodeService.removeAssociation(
				sourceRef, 
				targetRef, 
				assocType);
	}
	
	
	@Override
	public List<NodeRef> getTargetAssocs(NodeRef nodeRef, ManyToManyAssociationModel assoc) {
		return _getTargetAssocs(nodeRef, assoc);
	}
	@Override
	public List<NodeRef> getSourceAssocs(NodeRef nodeRef, ManyToManyAssociationModel assoc) {
		return _getSourceAssocs(nodeRef, assoc);
	}
	@Override
	public Optional<NodeRef> getTargetAssocs(NodeRef nodeRef, ManyToOneAssociationModel assoc) {
		return toOptional(_getTargetAssocs(nodeRef, assoc));
	}
	@Override
	public List<NodeRef> getSourceAssocs(NodeRef nodeRef, ManyToOneAssociationModel assoc) {
		return _getSourceAssocs(nodeRef, assoc);
	}
	@Override
	public List<NodeRef> getTargetAssocs(NodeRef nodeRef, OneToManyAssociationModel assoc) {
		return _getTargetAssocs(nodeRef, assoc);
	}
	@Override
	public Optional<NodeRef> getSourceAssocs(NodeRef nodeRef, OneToManyAssociationModel assoc) {
		return toOptional(_getSourceAssocs(nodeRef, assoc));
	}
	@Override
	public Optional<NodeRef> getTargetAssocs(NodeRef nodeRef, OneToOneAssociationModel assoc) {
		return toOptional(_getTargetAssocs(nodeRef, assoc));
	}
	@Override
	public Optional<NodeRef> getSourceAssocs(NodeRef nodeRef, OneToOneAssociationModel assoc) {
		return getSourceAssocs(nodeRef, assoc);
	}
	private List<NodeRef> _getTargetAssocs(NodeRef nodeRef, AssociationModel assoc) {
		return nodeService.getTargetAssocs(
				nodeRef, 
				assoc.getQName()).stream()
			.map(assocRef -> assocRef.getTargetRef())
			.collect(Collectors.toList());
	}
	private List<NodeRef> _getSourceAssocs(NodeRef nodeRef, AssociationModel assoc) {
		return nodeService.getSourceAssocs(
				nodeRef, 
				assoc.getQName()).stream()
			.map(assocRef -> assocRef.getSourceRef())
			.collect(Collectors.toList());
	}
	private <T> Optional<T> toOptional(List<T> list) {
		if (list.isEmpty()) return Optional.empty();
		if (list.size() == 1) return Optional.of(list.get(0));
		throw new IllegalStateException("list=" + list);
	}
	
	
	@Override
	public NodeRef getCompanyHome() {
		return repositoryHelper.getCompanyHome();
	}
	@Override
	public NodeRef getSharedHome() {
		return repositoryHelper.getSharedHome();
	}
	@Override
	public NodeRef getDataDictionary() {
		NodeRef dataDictionaryRef = singletonCache.get(KEY_DATADICTIONARY_NODEREF);
		if (dataDictionaryRef == null) {
			dataDictionaryRef = AuthenticationUtil.runAs(new RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					NodeRef parent = getCompanyHome();
					return getChildAssocs(parent, CmModel.folder.contains, QName.createQName(dataDictionaryChildName, namespaceDAO)).get();
				}
			}, AuthenticationUtil.getSystemUserName());

			singletonCache.put(KEY_DATADICTIONARY_NODEREF, dataDictionaryRef);
		}
		return dataDictionaryRef;
	}
	
	/**
	 * Retourne le home folder de l'utilisateur en cours. 
	 * Cela ne tient pas compte de possible runAs.
	 */
	@Override
	public Optional<NodeRef> getUserHome() {
		NodeRef person = repositoryHelper.getFullyAuthenticatedPerson();
		return Optional.ofNullable(repositoryHelper.getUserHome(person));
	}

	/**
	 * Recherche un noeud à partir d'un chemin. Le chemin est défini en dessous de "Company Home",
	 * avec une liste de cm:name. 
	 * Cette méthode n'utilise pas de recherche Lucene.
	 */
	@Override
	public Optional<NodeRef> getByNamedPath(String ... names) {
		NodeRef nodeRef = repositoryHelper.getCompanyHome();
		for (String name : names) {
			if (! name.isEmpty()) {
				Optional<NodeRef> optional = getChildByName(nodeRef, name);
				if (! optional.isPresent()) {
					return optional;
				}
				nodeRef = optional.get();
			}
		}
		return Optional.of(nodeRef);
	}
	
	@Override
	public String getPath(NodeRef nodeRef) {
		return nodeService.getPath(nodeRef).toString();
	}

	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}
	public void setDataDictionaryChildName(String dataDictionaryChildName) {
		this.dataDictionaryChildName = dataDictionaryChildName;
	}
	public void setSingletonCache(SimpleCache<String, NodeRef> singletonCache) {
		this.singletonCache = singletonCache;
	}

}
