package fr.openwide.alfresco.repo.dictionary.node.service.impl;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.alfresco.repo.cache.SimpleCache;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.AuthenticationUtil.RunAsWork;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
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
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
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
	private Repository repositoryHelper;

	private ConversionService conversionService;
	
	private String dataDictionaryChildName;
	private SimpleCache<String, NodeReference> singletonCache; // eg. for dataDictionaryNodeRef
	private final String KEY_DATADICTIONARY_NODEREF = "owsi.key.datadictionary.noderef";
	
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
	public Optional<NodeReference> getChildAssocs(NodeReference nodeReference, ChildAssociationModel associationType, NameReference assocName) {
		List<ChildAssociationRef> children = nodeService.getChildAssocs(
				conversionService.getRequired(nodeReference), 
				conversionService.getRequired(associationType.getNameReference()), 
				conversionService.getRequired(assocName));
		return Optional.fromNullable((children.isEmpty()) 
				? null
				: conversionService.get(children.get(0).getChildRef()));
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
	public NodeReference getCompanyHome() {
		return conversionService.get(repositoryHelper.getCompanyHome());
	}
	@Override
	public NodeReference getDataDictionary() {
		NodeReference dataDictionaryRef = singletonCache.get(KEY_DATADICTIONARY_NODEREF);
		if (dataDictionaryRef == null) {
			dataDictionaryRef = AuthenticationUtil.runAs(new RunAsWork<NodeReference>() {
				@Override
				public NodeReference doWork() throws Exception {
					NodeReference parent = getCompanyHome();
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
	public Optional<NodeReference> getUserHome() {
		NodeRef person = repositoryHelper.getFullyAuthenticatedPerson();
		NodeRef userHome = repositoryHelper.getUserHome(person);
		if (userHome != null) {
			return Optional.of(conversionService.get(userHome));
		} else {
			return Optional.absent();
		}
	}

	/**
	 * Recherche un noeud à partir d'un chemin. Le chemin est défini en dessous de "Company Home",
	 * avec une liste de cm:name. 
	 * Cette méthode n'utilise pas de recherche Lucene.
	 */
	@Override
	public Optional<NodeReference> getByNamedPath(String ... names) {
		NodeReference nodeReference = conversionService.get(repositoryHelper.getCompanyHome());
		for (String name : names) {
			if (! name.isEmpty()) {
				Optional<NodeReference> optional = getChildByName(nodeReference, name);
				if (! optional.isPresent()) {
					return optional;
				}
				nodeReference = optional.get();
			}
		}
		return Optional.of(nodeReference);
	}

	@Override
	public String getPath(NodeReference nodeReference) {
		return get(nodeReference, new NodeScopeBuilder().path()).getPath();
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setCopyService(CopyService copyService) {
		this.copyService = copyService;
	}
	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}
	public void setDataDictionaryChildName(String dataDictionaryChildName) {
		this.dataDictionaryChildName = dataDictionaryChildName;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setSingletonCache(SimpleCache<String, NodeReference> singletonCache) {
		this.singletonCache = singletonCache;
	}

}
