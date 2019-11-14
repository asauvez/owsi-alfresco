package fr.openwide.alfresco.repo.treeaspect.service.impl;

import fr.openwide.alfresco.repo.treeaspect.service.RegisterRootPropertyName;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.*;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.Serializable;
import java.util.*;

public class RegisterRootPropertyNameImpl implements RegisterRootPropertyName, OnUpdatePropertiesPolicy {

	private static final Logger LOGGER = LoggerFactory.getLogger(RegisterRootPropertyNameImpl.class);


	@Autowired private PolicyComponent policyComponent;
	@Autowired private NodeService nodeService;


	private static class PropertiesForCopy {
		public QName aspectForCopy;
		public QName propertyToCopy;
		public QName propertyWhereCopy;

		public PropertiesForCopy(QName aspectForCopy, QName propertyToCopy, QName propertyWhereCopy) {
			this.aspectForCopy = aspectForCopy;
			this.propertyToCopy = propertyToCopy;
			this.propertyWhereCopy = propertyWhereCopy;
		}
	}

	private List<PropertiesForCopy> registerRootPropertyName = new ArrayList<>();

	@Override public void registerCopyPropertyName(QName aspectOfRootNode, QName propertyWhereCopy) {
		registerCopyPropertyName(aspectOfRootNode, ContentModel.PROP_NAME, propertyWhereCopy);
	}

	@Override public void registerCopyPropertyName(QName aspectOfRootNode, QName propertyToCopy, QName propertyWhereCopy) {
		policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME,
				aspectOfRootNode,
				new JavaBehaviour(this, OnUpdatePropertiesPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
		PropertiesForCopy propertiesForCopy = new PropertiesForCopy(aspectOfRootNode, propertyToCopy, propertyWhereCopy);
		registerRootPropertyName.add(propertiesForCopy);
	}


	@Override public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		LOGGER.debug("Start onUpdateProperties");
		if (! nodeService.exists(nodeRef)) return;
		if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_THUMBNAIL)) return;

		for (PropertiesForCopy properties : registerRootPropertyName) {
			if (nodeService.hasAspect(nodeRef, properties.aspectForCopy)) {
				nodeService.setProperty(nodeRef, properties.propertyWhereCopy, nodeService.getProperty(nodeRef, properties.propertyToCopy));
			}
		}
		LOGGER.debug("End onUpdateProperties");
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}

	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

}
