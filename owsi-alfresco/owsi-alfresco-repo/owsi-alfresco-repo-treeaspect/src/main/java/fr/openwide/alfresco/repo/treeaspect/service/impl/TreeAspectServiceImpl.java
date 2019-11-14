package fr.openwide.alfresco.repo.treeaspect.service.impl;

import fr.openwide.alfresco.repo.treeaspect.service.TreeAspectService;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.*;
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

import java.io.Serializable;
import java.util.*;

public class TreeAspectServiceImpl implements TreeAspectService, InitializingBean, OnCreateNodePolicy, OnUpdatePropertiesPolicy,
		OnMoveNodePolicy, OnAddAspectPolicy, OnRemoveAspectPolicy {

	private static final Logger LOGGER = LoggerFactory.getLogger(TreeAspectServiceImpl.class);


	@Autowired private PolicyComponent policyComponent;
	@Autowired private NodeService nodeService;
	@Autowired private DictionaryService dictionaryService;
	@Autowired @Qualifier("policyBehaviourFilter") private BehaviourFilter behaviourFilter;

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

		AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {
			for (ChildAssociationRef child : children) {

				NodeRef childRef = child.getChildRef();
				if (nodeService.getType(childRef).equals(ContentModel.TYPE_THUMBNAIL)) continue;

				Map<QName, Serializable> properties = new HashMap<>();


				Map<QName, PropertyDefinition> aspectProperty = dictionaryService.getAspect(newAspect).getProperties();
				for (QName property : aspectProperty.keySet()) {
					properties.put(property, nodeService.getProperty(nodeRef, property));
				}
				try {
					behaviourFilter.disableBehaviour(childRef);
					nodeService.addAspect(childRef, newAspect, properties);
				} finally {
					behaviourFilter.enableBehaviour(childRef);
				}
			}
			return null;
		}, AuthenticationUtil.getSystemUserName());

		LOGGER.debug("End onAddAspect");
	}
	
	@Override public void onCreateNode(ChildAssociationRef childAssocRef) {
		LOGGER.debug("Start onCreateNode");
		if (! nodeService.exists(childAssocRef.getChildRef()) && ! nodeService.exists(childAssocRef.getParentRef())) return;
		// TODO : ne rien faire si type = cm:thumbnail


		NodeRef parentRef = childAssocRef.getParentRef();
		NodeRef childRef = childAssocRef.getChildRef();
		AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {
			copyParentAspectToChild(parentRef, childRef);
			return null;
		}, AuthenticationUtil.getSystemUserName());

		LOGGER.debug("End onCreateNode");
	}

	private void copyParentAspectToChild(NodeRef parentRef, NodeRef childRef) {
		LOGGER.debug("Start copyParentAspectToChild");

		if (!nodeService.exists(childRef)) return;
		if (nodeService.getType(childRef).equals(ContentModel.TYPE_THUMBNAIL)) return;

		AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {

			for (QName aspect : aspectToCopy.keySet()) {
				copyAspectToNode(parentRef, childRef, aspect);
			}
			List<ChildAssociationRef> children = nodeService.getChildAssocs(childRef);
			if (children.size() > 0 ) {
				for (ChildAssociationRef child : children) {
					copyParentAspectToChild(childRef, child.getChildRef());
				}
			}
			return null;
		}, AuthenticationUtil.getSystemUserName());

		LOGGER.debug("End copyParentAspectToChild");
	}

	private void copyAspectToNode(NodeRef parentRef, NodeRef childRef, QName aspect) {
		if (nodeService.hasAspect(parentRef, aspect)) {

			Map<QName, PropertyDefinition> aspectProperty = dictionaryService.getAspect(aspect).getProperties();

			Map<QName, Serializable> aspectProperties = new HashMap<>();
			for (QName property : aspectProperty.keySet()) {
				aspectProperties.put(property, nodeService.getProperty(parentRef, property));
			}
			try {
				behaviourFilter.disableBehaviour(childRef);
				nodeService.addAspect(childRef, aspect, aspectProperties);
			} finally {
				behaviourFilter.enableBehaviour(childRef);
			}
		}
	}

	@Override public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		// TODO ignore version + auditable policy
		LOGGER.debug("Start onUpdateProperties");
		if (! nodeService.exists(nodeRef)) return;
		if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_THUMBNAIL)) return;

		AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {

			for (QName aspect : aspectToCopy.keySet()) {
				Map<QName, PropertyDefinition> aspectProperties = dictionaryService.getAspect(aspect).getProperties();

				for (QName property : aspectProperties.keySet()) {
					Serializable beforeProperty = before.get(property);
					Serializable afterProperty = after.get(property);
					if (!Objects.equals(beforeProperty, afterProperty)) {

						for (ChildAssociationRef child : nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CONTAINS, null)) {
							NodeRef childRef = child.getChildRef();

							try {
								behaviourFilter.disableBehaviour(childRef);
								nodeService.setProperty(childRef, property, afterProperty);
							} finally {
								behaviourFilter.enableBehaviour(childRef);
							}
						}
					}
				}
			}
			return null;
		}, AuthenticationUtil.getSystemUserName());

		LOGGER.debug("End onUpdateProperties");
	}

	@Override public void onMoveNode(ChildAssociationRef oldChildAssocRef, ChildAssociationRef newChildAssocRef) {
		LOGGER.debug("Start onMoveNode");

		NodeRef newChild = newChildAssocRef.getChildRef();
		NodeRef newParent = newChildAssocRef.getParentRef();

		if (!nodeService.exists(newChild)) return;
		if (nodeService.getType(newChild).equals(ContentModel.TYPE_THUMBNAIL)) return;


		AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {
			for (QName aspect : aspectToCopy.keySet()) {
				if (nodeService.hasAspect(newChild, aspect) && !nodeService.hasAspect(newParent, aspect)) {
					// Si le parent n'a pas l'aspect, c'est que le node en question est le node root
					if (!nodeService.hasAspect(oldChildAssocRef.getParentRef(), aspect)) {
						LOGGER.debug("End onMoveNode nothing to do because is root aspect");
						return null;
					}

					behaviourFilter.disableBehaviour(newChild);
					if (aspectToCopy.get(aspect)) {
						nodeService.removeAspect(newChild, aspect);
					}
					behaviourFilter.enableBehaviour(newChild);
				}
				copyAspectToNode(newParent, newChild, aspect);
			}
			return null;
		}, AuthenticationUtil.getSystemUserName());

		LOGGER.debug("End onMoveNode");
	}

	@Override public void onRemoveAspect(NodeRef nodeRef, QName aspectTypeQName) {
		LOGGER.debug("Start onRemoveAspect");
		if (! nodeService.exists(nodeRef)) return;

		AuthenticationUtil.runAs((AuthenticationUtil.RunAsWork<Void>) () -> {
			HashMap<QName, Serializable> versionableProperties = new HashMap<>();
			for (ChildAssociationRef child : nodeService.getChildAssocs(nodeRef)) {
				NodeRef childRef = child.getChildRef();
				try {
					behaviourFilter.disableBehaviour(childRef);
					nodeService.removeAspect(childRef, aspectTypeQName);
				} finally {
					behaviourFilter.enableBehaviour(childRef);
				}
			}
			return null;
		}, AuthenticationUtil.getSystemUserName());

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
