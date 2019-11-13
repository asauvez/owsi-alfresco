package fr.openwide.alfresco.repo.treeaspect.service.impl;

import fr.openwide.alfresco.repo.treeaspect.service.TreeAspectService;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnAddAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnMoveNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnRemoveAspectPolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
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

import java.io.Serializable;
import java.util.*;

public class TreeAspectServiceImpl implements TreeAspectService, InitializingBean, OnCreateNodePolicy, OnUpdatePropertiesPolicy,
		OnMoveNodePolicy, OnAddAspectPolicy, OnRemoveAspectPolicy {

	private static final Logger LOGGER = LoggerFactory.getLogger(TreeAspectServiceImpl.class);


	@Autowired private PolicyComponent policyComponent;
	@Autowired private NodeService nodeService;
	@Autowired private DictionaryService dictionaryService;

	@Autowired @Qualifier("global-properties")
	private Properties properties;
//	@Autowired private String aspectToRegister = (String) properties.get("aspect.to.copy");

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
		LOGGER.debug("Start onAddAspect");
		if (! nodeService.exists(nodeRef)) return;
		if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_THUMBNAIL)) return;

		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);

		for (ChildAssociationRef child : children) {

			if (nodeService.getType(child.getChildRef()).equals(ContentModel.TYPE_THUMBNAIL)) continue;

			Map<QName, Serializable> properties = new HashMap<>();


			Map<QName, PropertyDefinition> aspectProperty = dictionaryService.getAspect(newAspect).getProperties();
			for (QName property : aspectProperty.keySet()) {
				properties.put(property, nodeService.getProperty(nodeRef, property));
			}
			nodeService.addAspect(child.getChildRef(), newAspect, properties);
		}

//		copyToChild(nodeRef, nodeService.getProperties(nodeRef));
		LOGGER.debug("End onAddAspect");
	}
	
	@Override public void onCreateNode(ChildAssociationRef childAssocRef) {
		LOGGER.debug("Start onCreateNode");
		if (! nodeService.exists(childAssocRef.getChildRef()) && ! nodeService.exists(childAssocRef.getParentRef())) return;
		// TODO : ne rien faire si type = cm:thumbnail


		NodeRef parentRef = childAssocRef.getParentRef();
		NodeRef childRef = childAssocRef.getChildRef();
		copyParentAspectToChild(parentRef, childRef);

		LOGGER.debug("End onCreateNode");
	}

	private void copyParentAspectToChild(NodeRef parentRef, NodeRef childRef) {
		LOGGER.debug("Start copyParentAspectToChild");

		if (!nodeService.exists(childRef)) return;
		if (nodeService.getType(childRef).equals(ContentModel.TYPE_THUMBNAIL)) return;

		for (QName aspect : aspectToCopy.keySet()) {
			if (nodeService.hasAspect(parentRef, aspect)) {
				Map<QName, PropertyDefinition> aspectProperty = dictionaryService.getAspect(aspect).getProperties();

				for (QName property : aspectProperty.keySet()) {
					nodeService.setProperty(childRef, property, nodeService.getProperty(parentRef, property));
				}
			}
		}
		List<ChildAssociationRef> children = nodeService.getChildAssocs(childRef);
		if (children.size() > 0 ) {
			for (ChildAssociationRef child : children) {
				copyParentAspectToChild(childRef, child.getChildRef());
			}
		}
		LOGGER.debug("End copyParentAspectToChild");
	}

	@Override public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		// TODO ignore version + auditable policy
		LOGGER.debug("Start onUpdateProperties");
		if (! nodeService.exists(nodeRef)) return;
		if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_THUMBNAIL)) return;

		for (QName aspect : aspectToCopy.keySet()) {
			Map<QName, PropertyDefinition> aspectProperties = dictionaryService.getAspect(aspect).getProperties();

			for (QName property : aspectProperties.keySet()) {
				Serializable beforeProperty = before.get(property);
				Serializable afterProperty = after.get(property);
				if (beforeProperty != null && !beforeProperty.equals(afterProperty)) {
					for (ChildAssociationRef child : nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, null)) {
						nodeService.setProperty(child.getChildRef(), property, afterProperty);
					}
				}
			}
		}
		
		LOGGER.debug("End onUpdateProperties");
	}

	@Override public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
		LOGGER.debug("Start onMoveNode");

		NodeRef newChild = newChildAssocRef.getChildRef();
		NodeRef newParent = newChildAssocRef.getParentRef();

		if (!nodeService.exists(newChild)) return;
		if (nodeService.getType(newChild).equals(ContentModel.TYPE_THUMBNAIL)) return;

		for (QName aspect : aspectToCopy.keySet()) {
			if (nodeService.hasAspect(newChild, aspect) && !nodeService.hasAspect(newParent, aspect)) {
				// Si le parent n'a pas l'aspect, c'est que le node en question est le node root
				if (!nodeService.hasAspect(oldChildAssocRef.getParentRef(), aspect)) {
					LOGGER.debug("End onMoveNode nothing to do because is root aspect");
					return;
				}

				if (aspectToCopy.get(aspect)) {
					nodeService.removeAspect(newChild, aspect);
				}
			}
			if (nodeService.hasAspect(newParent, aspect)) {
				if (nodeService.hasAspect(newChild, aspect)) {
					nodeService.removeAspect(newChild, aspect);
				}

				Map<QName, PropertyDefinition> aspectProperty = dictionaryService.getAspect(aspect).getProperties();

				for (QName property : aspectProperty.keySet()) {
					nodeService.setProperty(newChild, property, nodeService.getProperty(newParent, property));
				}
			}
			Map<QName, Serializable> toto = nodeService.getProperties(newChild);
		}

		LOGGER.debug("End onMoveNode");
	}

	@Override public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName) {
		LOGGER.debug("Start onRemoveAspect");
		if (! nodeService.exists(nodeRef)) return;

		for (ChildAssociationRef child : nodeService.getChildAssocs(nodeRef)) {
			nodeService.removeAspect(child.getChildRef(), aspectTypeQName);
		}
		
		LOGGER.debug("Start onRemoveAspect");
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setDictionaryService(DictionaryService dictionaryService) {
		this.dictionaryService = dictionaryService;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}

	public void setAspectToCopy(Map<QName, Boolean> aspectToCopy) {
		this.aspectToCopy = aspectToCopy;
	}
}
