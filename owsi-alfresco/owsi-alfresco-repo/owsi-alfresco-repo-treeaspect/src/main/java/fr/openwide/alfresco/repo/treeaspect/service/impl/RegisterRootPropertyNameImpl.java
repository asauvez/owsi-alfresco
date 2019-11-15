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

/**
 * Permet de copier une propriété dans une autre, si un aspect précit est présent.
 *
 * En appelant registerCopyPropertyName(QName aspectOfRootNode, QName propertyWhereCopy) copy le cm:name dans "propertyWhereCopy", quand
 * un node a l'aspect "aspectOfRootNode"
 * En appelant registerCopyPropertyName(QName aspectOfRootNode, QName propertyToCopy, QName propertyWhereCopy) copy la propiété
 * "propertyToCopy" dans "propertyWhereCopy", quand un node a l'aspect "aspectOfRootNode"
 *
 * @author recol
 */

public class RegisterRootPropertyNameImpl implements RegisterRootPropertyName, OnUpdatePropertiesPolicy, OnAddAspectPolicy {

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

	@Override public void registerCopyPropertyCmName(QName aspectOfRootNode, QName propertyWhereCopy) {
		registerCopyProperty(aspectOfRootNode, ContentModel.PROP_NAME, propertyWhereCopy);
	}

	@Override public void registerCopyProperty(QName aspectOfRootNode, QName propertyToCopy, QName propertyWhereCopy) {
		policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME,
				aspectOfRootNode,
				new JavaBehaviour(this, OnUpdatePropertiesPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));

		policyComponent.bindClassBehaviour(OnAddAspectPolicy.QNAME,
				aspectOfRootNode,
				new JavaBehaviour(this, OnAddAspectPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));

		PropertiesForCopy propertiesForCopy = new PropertiesForCopy(aspectOfRootNode, propertyToCopy, propertyWhereCopy);
		registerRootPropertyName.add(propertiesForCopy);
	}


	@Override public void onAddAspect(NodeRef nodeRef, QName aspectTypeQName) {
		copyProperties("onAddAspect", nodeRef);
	}

	@Override public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		copyProperties("onUpdateProperties" , nodeRef);
	}

	private void copyProperties(String methodeName, NodeRef nodeRef) {
		LOGGER.debug("Start " + methodeName +"() : " + nodeRef);
		if (! nodeService.exists(nodeRef)) return;
		if (nodeService.getType(nodeRef).equals(ContentModel.TYPE_THUMBNAIL)) return;

		for (PropertiesForCopy properties : registerRootPropertyName) {
			if (nodeService.hasAspect(nodeRef, properties.aspectForCopy)) {
				nodeService.setProperty(nodeRef, properties.propertyWhereCopy, nodeService.getProperty(nodeRef, properties.propertyToCopy));
			}
		}
		LOGGER.debug("End " + methodeName +"()");
	}
}
