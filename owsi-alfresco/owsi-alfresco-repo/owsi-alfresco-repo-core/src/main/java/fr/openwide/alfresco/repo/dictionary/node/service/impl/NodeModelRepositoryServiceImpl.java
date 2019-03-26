package fr.openwide.alfresco.repo.dictionary.node.service.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.CopyService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.node.exception.DuplicateChildNodeNameRemoteException;
import fr.openwide.alfresco.api.core.remote.exception.IllegalStateRemoteException;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.ChildAssociationModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.association.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.ManyToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToManyAssociationModel;
import fr.openwide.alfresco.component.model.node.model.association.OneToOneAssociationModel;
import fr.openwide.alfresco.component.model.node.model.embed.PropertiesNode;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NodeReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.repo.core.node.service.impl.NodeRemoteServiceImpl;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;

public class NodeModelRepositoryServiceImpl implements NodeModelRepositoryService {

	@Autowired private NodeModelService nodeModelService;
	@Autowired private NodeService nodeService;
	@Autowired private CopyService copyService;
	private Repository repositoryHelper;

	@Autowired private ConversionService conversionService;
	
	private String dataDictionaryChildName;
	private SimpleCache<String, NodeRef> singletonCache; // eg. for dataDictionaryNodeRef
	private final String KEY_DATADICTIONARY_NODEREF = "owsi.key.datadictionary.noderef";
	
	@Override
	public NodeRef createFolder(NodeRef parentRef, String folderName) throws DuplicateChildNodeNameRemoteException {
		return conversionService.getRequired(nodeModelService.createFolder(conversionService.get(parentRef), folderName));
	}
	
	@Override
	public boolean exists(NodeRef nodeRef) {
		return nodeService.exists(nodeRef);
	}
	
	@Override
	public void moveNode(NodeRef nodeRef, NodeRef newParentRef) {
		String nodeName = getProperty(nodeRef, CmModel.object.name);
		nodeService.moveNode(nodeRef, 
				newParentRef, 
				conversionService.getRequired(CmModel.folder.contains.getNameReference()), 
				NodeRemoteServiceImpl.createAssociationName(nodeName));
	}

	@Override
	public NodeRef copy(NodeRef nodeRef, NodeRef newParentRef, Optional<String> newName) {
		ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
		QName name = newName.isPresent() 
				? NodeRemoteServiceImpl.createAssociationName(newName.get())
				: primaryParent.getQName();
		NodeRef copy = copyService.copy(
			nodeRef, 
			newParentRef, 
			primaryParent.getTypeQName(), 
			name, 
			true);
		nodeService.setProperty(copy, ContentModel.PROP_NAME, newName.isPresent() 
				? newName.get()
				: nodeService.getProperty(nodeRef, ContentModel.PROP_NAME));
		return copy;
	}

	@Override
	public boolean isType(NodeRef nodeRef, TypeModel typeModel) {
		return typeModel.getNameReference().equals(getType(nodeRef));
	}
	@Override
	public NameReference getType(NodeRef nodeRef) {
		return conversionService.get(nodeService.getType(nodeRef));
	}

	@Override
	public void setType(NodeRef nodeRef, NameReference type) {
		nodeService.setType(nodeRef, conversionService.getRequired(type));
	}
	@Override
	public void setType(NodeRef nodeRef, TypeModel type) {
		setType(nodeRef, type.getNameReference());
	}

	@Override
	public Set<NameReference> getAspects(NodeRef nodeRef) {
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		Set<NameReference> nameReferences = new LinkedHashSet<>();
		for (QName aspect : aspects) {
			nameReferences.add(conversionService.get(aspect));
		}
		return nameReferences;
	}

	@Override
	public boolean hasAspect(NodeRef nodeRef, NameReference aspect) {
		return nodeService.hasAspect(nodeRef, conversionService.getRequired(aspect));
	}
	@Override
	public boolean hasAspect(NodeRef nodeRef, AspectModel aspect) {
		return hasAspect(nodeRef, aspect.getNameReference());
	}

	@Override
	public void addAspect(NodeRef nodeRef, AspectModel aspect) {
		addAspect(nodeRef, aspect, new BusinessNode());
	}
	@Override
	public void addAspect(NodeRef nodeRef, NameReference aspect) {
		addAspect(nodeRef, aspect, new BusinessNode());
	}
	@Override
	public void addAspect(NodeRef nodeRef, NameReference aspect, BusinessNode node) {
		nodeService.addAspect(nodeRef, 
				conversionService.getRequired(aspect), 
				conversionService.getForRepository(node.getRepositoryNode().getProperties()));
	}
	@Override
	public void addAspect(NodeRef nodeRef, AspectModel aspect, BusinessNode node) {
		addAspect(nodeRef, aspect.getNameReference(), node);
	}
	
	@Override
	public void removeAspect(NodeRef nodeRef, NameReference aspect) {
		nodeService.removeAspect(nodeRef, 
				conversionService.getRequired(aspect));
	}
	@Override
	public void removeAspect(NodeRef nodeRef, AspectModel aspect) {
		removeAspect(nodeRef, aspect.getNameReference());
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
	public Map<NameReference, Serializable> getProperties(NodeRef nodeRef) {
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		Map<NameReference, Serializable> res = new LinkedHashMap<>();
		for (Entry<QName, Serializable> entry : properties.entrySet()) {
			res.put(conversionService.get(entry.getKey()), entry.getValue());
		}
		return res;
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(NodeRef nodeRef, NameReference property) {
		return (C) conversionService.getForApplication(nodeService.getProperty(
				nodeRef,
				conversionService.getRequired(property)));
	}

	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> C getProperty(NodeRef nodeRef, SinglePropertyModel<C> property) {
		return (C) getProperty(nodeRef, property.getNameReference());
	}
	@Override
	public NodeRef getProperty(NodeRef nodeRef, NodeReferencePropertyModel property) {
		return conversionService.getRequired((NodeReference) getProperty(nodeRef, property.getNameReference()));
	}
	@Override
	public <E extends Enum<E>> E getProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property) {
		return PropertiesNode.textToEnum(property, (String) getProperty(nodeRef, property.getNameReference()));
	}
	@Override
	@SuppressWarnings("unchecked")
	public <C extends Serializable> List<C> getProperty(NodeRef nodeRef, MultiPropertyModel<C> property) {
		return (List<C>) getProperty(nodeRef, property.getNameReference());
	}
	@Override
	@SuppressWarnings("unchecked")
	public List<NodeRef> getProperty(NodeRef nodeRef, MultiNodeReferencePropertyModel property) {
		return (List<NodeRef>) getProperty(nodeRef, property.getNameReference());
	}

	@Override
	public <C extends Serializable> void setProperty(NodeRef nodeRef, SinglePropertyModel<C> property, C value) {
		setProperty(nodeRef, property.getNameReference(), value);
	}
	@Override
	public void setProperty(NodeRef nodeRef, NodeReferencePropertyModel property, NodeRef value) {
		setProperty(nodeRef, property.getNameReference(), conversionService.get(nodeRef));
	}
	@Override
	public <E extends Enum<E>> void setProperty(NodeRef nodeRef, EnumTextPropertyModel<E> property, E value) {
		String code = PropertiesNode.enumToText(value);
		setProperty(nodeRef, property.getNameReference(), code);
	}
	@Override
	public <C extends Serializable> void setProperty(NodeRef nodeRef, MultiPropertyModel<C> property, List<C> value) {
		setProperty(nodeRef, property.getNameReference(), (Serializable) value);
	}
	@Override
	public void setProperty(NodeRef nodeRef, MultiNodeReferencePropertyModel property, List<NodeRef> value) {
		setProperty(nodeRef, property.getNameReference(), (Serializable) value);
	}

	@Override
	public <C extends Serializable> void setProperty(NodeRef nodeRef, NameReference property, C value) {
		nodeService.setProperty(
				nodeRef,
				conversionService.getRequired(property),
				conversionService.getForRepository(value));
	}
	@Override
	public <C extends Serializable>  void removeProperty(NodeRef nodeRef, SinglePropertyModel<C> property) {
		nodeService.removeProperty(nodeRef,conversionService.getRequired( property.getNameReference()));
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
	public Optional<NodeRef> getChildAssocs(NodeRef nodeRef, ChildAssociationModel associationType, NameReference assocName) {
		List<ChildAssociationRef> children = nodeService.getChildAssocs(
				nodeRef, 
				conversionService.getRequired(associationType.getNameReference()), 
				conversionService.getRequired(assocName));
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
				conversionService.getRequired(associationType.getNameReference()), 
				RegexQNamePattern.MATCH_ALL);
		return children.stream().map(child -> child.getChildRef())
				.collect(Collectors.toList());
	}
	@Override
	public Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName) {
		return getChildByName(nodeRef, childName, CmModel.folder.contains);
	}
	@Override
	public Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, ChildAssociationModel associationType) {
		return getChildByName(nodeRef, childName, associationType.getNameReference());
	}

	@Override
	public Optional<NodeRef> getChildByName(NodeRef nodeRef, String childName, NameReference associationType) {
		NodeRef subNodeRef = nodeService.getChildByName(
				nodeRef, 
				conversionService.getRequired(associationType), 
				QName.createValidLocalName(childName));
		NodeRef subnodeRef = (subNodeRef != null) ? subNodeRef : null;
		return Optional.ofNullable(subnodeRef);
	}
	
	@Override
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
	public String getUniqueChildName(NodeRef folder, NodeRef document) {
		String originalName = getProperty(document, CmModel.object.name);
		Optional<NodeRef> childByName = getChildByName(folder, originalName);
		if (! childByName.isPresent() || childByName.get().equals(document)) {
			return originalName;
		}
		return getUniqueChildName(folder, originalName);
	}
	
	@Override
	public void addChild(NodeRef parentRef, NodeRef childRef) {
		addChild(parentRef, childRef, CmModel.folder.contains);
	}
	@Override
	public void addChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType) {
		addChild(parentRef, childRef, assocType.getNameReference());
	}
	@Override
	public void addChild(NodeRef parentRef, NodeRef childRef, NameReference assocType) {
		String childName = getProperty(childRef, CmModel.object.name);
		nodeService.addChild(parentRef, 
				childRef, 
				conversionService.getRequired(assocType), 
				NodeRemoteServiceImpl.createAssociationName(childName));
	}
	
	@Override
	public void removeChild(NodeRef parentRef, NodeRef childRef) {
		removeChild(parentRef, childRef, CmModel.folder.contains);
	}
	@Override
	public void removeChild(NodeRef parentRef, NodeRef childRef, ChildAssociationModel assocType) {
		removeChild(parentRef, childRef, assocType.getNameReference());
	}
	@Override
	public void removeChild(NodeRef parentRef, NodeRef childRef, NameReference assocType) {
		String childName = getProperty(childRef, CmModel.object.name);
		List<ChildAssociationRef> assocs = nodeService.getChildAssocs(
				parentRef, 
				conversionService.getRequired(assocType), 
				NodeRemoteServiceImpl.createAssociationName(childName));
		for (ChildAssociationRef assoc : assocs) {
			if (assoc.getChildRef().equals(childRef)) {
				nodeService.removeChildAssociation(assoc);
				return;
			}
		}
		throw new IllegalStateRemoteException("Can't find secondary child association " + parentRef + "/" + childRef);
	}
	@Override
	public void unlinkSecondaryParents(NodeRef nodeRef, ChildAssociationModel childAssociationModel) {
		List<ChildAssociationRef> parentAssocs = nodeService.getParentAssocs(
				nodeRef, 
				conversionService.getRequired(childAssociationModel.getNameReference()), 
				RegexQNamePattern.MATCH_ALL);
		for (ChildAssociationRef assoc : parentAssocs) {
			if (! assoc.isPrimary()) {
				nodeService.removeChildAssociation(assoc);
			}
		}
	}
	
	@Override
	public void createAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType) {
		createAssociation(sourceRef, targetRef, assocType.getNameReference());
	}
	@Override
	public void createAssociation(NodeRef sourceRef, NodeRef targetRef, NameReference assocType) {
		nodeService.createAssociation(
				sourceRef, 
				targetRef, 
				conversionService.getRequired(assocType));
	}
	@Override
	public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, AssociationModel assocType) {
		removeAssociation(sourceRef, targetRef, assocType.getNameReference());
	}
	@Override
	public void removeAssociation(NodeRef sourceRef, NodeRef targetRef, NameReference assocType) {
		nodeService.removeAssociation(
				sourceRef, 
				targetRef, 
				conversionService.getRequired(assocType));
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
				conversionService.getRequired(assoc.getNameReference())).stream()
			.map(assocRef -> assocRef.getTargetRef())
			.collect(Collectors.toList());
	}
	private List<NodeRef> _getSourceAssocs(NodeRef nodeRef, AssociationModel assoc) {
		return nodeService.getSourceAssocs(
				nodeRef, 
				conversionService.getRequired(assoc.getNameReference())).stream()
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
	public NodeRef getDataDictionary() {
		NodeRef dataDictionaryRef = singletonCache.get(KEY_DATADICTIONARY_NODEREF);
		if (dataDictionaryRef == null) {
			dataDictionaryRef = AuthenticationUtil.runAs(new RunAsWork<NodeRef>() {
				@Override
				public NodeRef doWork() throws Exception {
					NodeRef parent = getCompanyHome();
					return getChildAssocs(parent, CmModel.folder.contains, NameReference.create(dataDictionaryChildName)).get();
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
