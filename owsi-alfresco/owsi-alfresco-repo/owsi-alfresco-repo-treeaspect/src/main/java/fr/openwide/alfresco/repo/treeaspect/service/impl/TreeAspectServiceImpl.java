package fr.openwide.alfresco.repo.treeaspect.service.impl;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
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

public class TreeAspectServiceImpl implements TreeAspectService, InitializingBean, OnCreateNodePolicy, OnUpdatePropertiesPolicy,
		OnMoveNodePolicy, OnAddAspectPolicy, OnRemoveAspectPolicy {

	private static final Logger LOGGER = LoggerFactory.getLogger(TreeAspectServiceImpl.class);


	@Autowired private PolicyComponent policyComponent;
	@Autowired private NodeService nodeService;
	@Autowired private DictionaryService dictionaryService;
	@Autowired @Qualifier("policyBehaviourFilter") private BehaviourFilter behaviourFilter;

	private Map<QName, Boolean> aspectToCopy = new HashMap<>();

	@Override public void registerAspect(QName aspect) {
		registerAspect(aspect, true);
	}

	@Override public void registerAspect(QName aspect, boolean breakInheritanceDuringMove) {
		aspectToCopy.put(aspect, breakInheritanceDuringMove);

		policyComponent.bindClassBehaviour(OnAddAspectPolicy.QNAME,
				aspect,
				new JavaBehaviour(this, OnAddAspectPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
		policyComponent.bindClassBehaviour(OnRemoveAspectPolicy.QNAME,
				aspect,
				new JavaBehaviour(this, OnRemoveAspectPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
	}

	@Override
	public void afterPropertiesSet() throws Exception {
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
	
	@Override
	public void onAddAspect(final NodeRef nodeRef, QName newAspect) {
		runAsSystem("onAddAspect", nodeRef, () -> {
			List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, null);

			for (ChildAssociationRef child : children) {
				Map<QName, Serializable> properties = new HashMap<>();
				
				Map<QName, PropertyDefinition> aspectProperties = dictionaryService.getAspect(newAspect).getProperties();
				for (QName property : aspectProperties.keySet()) {
					properties.put(property, nodeService.getProperty(nodeRef, property));
				}
				nodeService.addAspect(child.getChildRef(), newAspect, properties);
			}
		});
	}
	
	@Override public void onCreateNode(ChildAssociationRef childAssocRef) {
		NodeRef parentRef = childAssocRef.getParentRef();
		NodeRef childRef = childAssocRef.getChildRef();
		
		if (! nodeService.exists(childAssocRef.getParentRef())) return;
		
		runAsSystem("onCreateNode", childRef, () -> {
			copyParentAspectToChild(parentRef, childRef);
		});
	}

	private void copyParentAspectToChild(NodeRef parentRef, NodeRef childRef) {
		for (QName aspect : aspectToCopy.keySet()) {
			copyAspectToNode(parentRef, childRef, aspect);
		}
		List<ChildAssociationRef> children = nodeService.getChildAssocs(childRef, ContentModel.ASSOC_CONTAINS, null);
		if (children.size() > 0 ) {
			for (ChildAssociationRef child : children) {
				
				// TODO besoin recurisif ???
				
				copyParentAspectToChild(childRef, child.getChildRef());
			}
		}
	}

	private void copyAspectToNode(NodeRef parentRef, NodeRef childRef, QName aspect) {
		if (nodeService.hasAspect(parentRef, aspect)) {
			Map<QName, PropertyDefinition> aspectProperties = dictionaryService.getAspect(aspect).getProperties();

			Map<QName, Serializable> newProperties = new HashMap<>();
			for (QName property : aspectProperties.keySet()) {
				Serializable value = nodeService.getProperty(parentRef, property);
				newProperties.put(property, value);
			}
			nodeService.addAspect(childRef, aspect, newProperties);
		}
	}

	@Override public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		runAsSystem("onUpdateProperties", nodeRef, () -> {
			List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, null);
			
			for (QName aspect : aspectToCopy.keySet()) {
				Map<QName, PropertyDefinition> aspectProperties = dictionaryService.getAspect(aspect).getProperties();

				for (QName property : aspectProperties.keySet()) {
					Serializable beforeProperty = before.get(property);
					Serializable afterProperty = after.get(property);
					if (!Objects.equals(beforeProperty, afterProperty)) {

						for (ChildAssociationRef child : childAssocs) {
							NodeRef childRef = child.getChildRef();
							nodeService.setProperty(childRef, property, afterProperty);
						}
					}
				}
			}
		});
	}

	@Override public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
		NodeRef newChild = newChildAssocRef.getChildRef();
		NodeRef newParent = newChildAssocRef.getParentRef();

		if (!nodeService.exists(newParent)) return;

		runAsSystem("onMoveNode", newChild, () -> {
			for (QName aspect : aspectToCopy.keySet()) {
				if (nodeService.hasAspect(newChild, aspect) && !nodeService.hasAspect(newParent, aspect)) {
					// Si le parent n'a pas l'aspect, c'est que le node en question est le node root
					if (!nodeService.hasAspect(oldChildAssocRef.getParentRef(), aspect)) {
						LOGGER.debug("End onMoveNode nothing to do because is root aspect");
						continue;
					}

					if (aspectToCopy.get(aspect)) {
						nodeService.removeAspect(newChild, aspect);
					}
				}
				copyAspectToNode(newParent, newChild, aspect);
			}
		});
	}

	@Override public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName) {
		runAsSystem("onRemoveAspect", nodeRef, () -> {
			for (ChildAssociationRef child : nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, null)) {
				nodeService.removeAspect(child.getChildRef(), aspectTypeQName);
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
