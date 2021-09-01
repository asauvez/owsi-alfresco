package fr.openwide.alfresco.repo.treeaspect.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnMoveNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.openwide.alfresco.repo.treeaspect.service.TreeAspectService;

/**
 * Permet de copier toutes les propriétés ainsi que les aspects dans les enfants d'un node.
 *
 * En appelant registerAspect(QName aspect) avec le QName de l'aspect, les policies permettant de mettre à jour les métadatas de cet aspect
 * La fonction registerAspect(QName aspect, boolean breakInheritanceDuringMove), en mettant breakInheritanceDuringMove à false, permettera
 * de ne pas supprimer les aspects quand on les bouge en dehors d'un dossier avec l'aspect.
 *
 * @author recol
 */

public class TreeAspectServiceImpl implements TreeAspectService, 
	InitializingBean,
	OnCreateNodePolicy, OnUpdatePropertiesPolicy, OnMoveNodePolicy, OnAddAspectPolicy, OnRemoveAspectPolicy {

	private static final Logger LOGGER = LoggerFactory.getLogger(TreeAspectServiceImpl.class);

	@Autowired private PolicyComponent policyComponent;
	@Autowired private NodeService nodeService;
	@Autowired private DictionaryService dictionaryService;
	@Autowired @Qualifier("policyBehaviourFilter") private BehaviourFilter behaviourFilter;
	@Autowired @Qualifier("NamespaceService") private NamespacePrefixResolver prefixResolver;
	@Autowired @Qualifier("global-properties") private Properties globalProperties;

	private Set<QName> aspectToCopy = new HashSet<>();

	@Override
	public void afterPropertiesSet() throws Exception {
		String aspectNames = globalProperties.getProperty("owsi.treeaspect.register", "");
		for (String aspectName : aspectNames.split(",")) {
			if (! aspectName.trim().isEmpty()) {
				registerAspect(QName.createQName(aspectName, prefixResolver));
			}
		}
	}
	
	@Override public void registerAspect(QName aspect) {
		registerAspect(aspect, true);
	}

	@Override public void registerAspect(QName aspect, boolean breakInheritanceDuringMove) {
		if (aspectToCopy.isEmpty()) {
			policyComponent.bindClassBehaviour(OnCreateNodePolicy.QNAME,
					ContentModel.TYPE_CMOBJECT,
					new JavaBehaviour(this, OnCreateNodePolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
			policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME,
					ContentModel.TYPE_CMOBJECT,
					new JavaBehaviour(this, OnUpdatePropertiesPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
			policyComponent.bindClassBehaviour(OnMoveNodePolicy.QNAME,
					ContentModel.TYPE_CMOBJECT,
					new JavaBehaviour(this, OnMoveNodePolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
		}
		
		if (! aspectToCopy.contains(aspect)) {
			aspectToCopy.add(aspect);
	
			policyComponent.bindClassBehaviour(OnAddAspectPolicy.QNAME,
					aspect,
					new JavaBehaviour(this, OnAddAspectPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
			policyComponent.bindClassBehaviour(OnRemoveAspectPolicy.QNAME,
					aspect,
					new JavaBehaviour(this, OnRemoveAspectPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
		}
	}
	
	@Override
	public void onAddAspect(final NodeRef nodeRef, QName newAspect) {
		runAsSystem("onAddAspect", nodeRef, () -> {
			if (! nodeService.exists(nodeRef)) return;

			List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);

			for (ChildAssociationRef child : children) {
				Map<QName, Serializable> properties = new HashMap<>();
				
				Map<QName, PropertyDefinition> aspectProperties = getProperties(newAspect);
				for (QName property : aspectProperties.keySet()) {
					properties.put(property, nodeService.getProperty(nodeRef, property));
				}
				NodeRef childRef = child.getChildRef();
				nodeService.addAspect(childRef, newAspect, properties);
				onAddAspect(childRef, newAspect);
			}
		});
	}
	
	@Override public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef parentRef = childAssocRef.getParentRef();
		NodeRef childRef = childAssocRef.getChildRef();
		
		runAsSystem("onCreateNode", childRef, () -> {
			if (! nodeService.exists(childAssocRef.getParentRef()) || ! nodeService.exists(childAssocRef.getChildRef())) return;
			
			for (QName aspect : aspectToCopy) {
				copyAspectToNode(parentRef, childRef, aspect);
			}
		});
	}

	private void copyAspectToNode(NodeRef parentRef, NodeRef childRef, QName aspect) {
		runAsSystem("copyAspectToNode", childRef, () -> {
			if (nodeService.hasAspect(parentRef, aspect)) {
				Map<QName, Serializable> nodeProperties = nodeService.getProperties(parentRef);
				Map<QName, PropertyDefinition> aspectProperties = getProperties(aspect);

				Map<QName, Serializable> newProperties = new HashMap<>();
				for (QName property : aspectProperties.keySet()) {
					if (nodeProperties.containsKey(property)) {
						newProperties.put(property, nodeProperties.get(property));
					}
				}
				nodeService.addAspect(childRef, aspect, newProperties);
				for (ChildAssociationRef childAssociationRef : nodeService.getChildAssocs(childRef)) {
					copyAspectToNode(childRef, childAssociationRef.getChildRef(), aspect);
				}
			}
		});
	}

	@Override public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		runAsSystem("onUpdateProperties", nodeRef, () -> {
			if (! nodeService.exists(nodeRef)) return;
			
			List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL);
			
			for (QName aspect : aspectToCopy) {
				Map<QName, PropertyDefinition> aspectProperties = getProperties(aspect);

				for (QName property : aspectProperties.keySet()) {
					Serializable beforeProperty = before.get(property);
					Serializable afterProperty = after.get(property);
					if (!Objects.equals(beforeProperty, afterProperty)) {
						updateChildProperties(childAssocs, property, afterProperty);
					}
				}
			}
		});
	}

	private Map<QName, PropertyDefinition> getProperties(QName aspect) {
		AspectDefinition aspectDefinition = dictionaryService.getAspect(aspect);
		Map<QName, PropertyDefinition> aspectProperties = new HashMap<>(aspectDefinition.getProperties());
		
		for (AspectDefinition mandatoryAspect : aspectDefinition.getDefaultAspects(true)) {
			aspectProperties.putAll(mandatoryAspect.getProperties());
		}
		
		return aspectProperties;
	}
	
	private void updateChildProperties(List<ChildAssociationRef> childAssocs, QName property, Serializable afterProperty) {
		for (ChildAssociationRef child : childAssocs) {
			NodeRef childRef = child.getChildRef();

			runAsSystem("updateChildProperties", childRef, () -> {
				nodeService.setProperty(childRef, property, afterProperty);
				updateChildProperties(nodeService.getChildAssocs(childRef), property, afterProperty);
			});
		}
	}

	@Override public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
		NodeRef newChild = newChildAssocRef.getChildRef();
		NodeRef newParent = newChildAssocRef.getParentRef();

		runAsSystem("onMoveNode", newChild, () -> {
			if (!nodeService.exists(newParent) || !nodeService.exists(newChild)) return;

			for (QName aspect : aspectToCopy) {
				copyAspectToNode(newParent, newChild, aspect);
			}
		});
	}

	@Override public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName) {
		runAsSystem("onRemoveAspect", nodeRef, () -> {
			if (! nodeService.exists(nodeRef)) return;
			
			for (ChildAssociationRef child : nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, RegexQNamePattern.MATCH_ALL)) {
				NodeRef childRef = child.getChildRef();
				nodeService.removeAspect(childRef, aspectTypeQName);
				onRemoveAspect(childRef, aspectTypeQName);
			}
		});
	}
	
	private void runAsSystem(String methodName, NodeRef nodeRef, Runnable runnable) {
		AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {
			if (! nodeService.exists(nodeRef)) return null;
			
			behaviourFilter.disableBehaviour(nodeRef);
			try {
				LOGGER.debug("Start " + methodName + "() : " + nodeRef);
				
				runnable.run();
				
				return null;
			} finally {
				behaviourFilter.enableBehaviour(nodeRef);
				LOGGER.debug("End " + methodName + "()");
			}
		}, AuthenticationUtil.getSystemUserName());
	}
}
