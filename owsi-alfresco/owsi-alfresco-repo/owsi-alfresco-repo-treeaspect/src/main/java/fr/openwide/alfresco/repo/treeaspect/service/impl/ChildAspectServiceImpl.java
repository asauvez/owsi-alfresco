package fr.openwide.alfresco.repo.treeaspect.service.impl;

import java.util.Properties;
import java.util.function.Consumer;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies.OnCreateChildAssociationPolicy;
import org.alfresco.repo.policy.BaseBehaviour;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import fr.openwide.alfresco.repo.treeaspect.service.ChildAspectService;

/**
 * Permet d'affecter un type ou un aspect aux enfants des nodes d'un type ou aspect donné.
 * 
 * owsi.childaspect.register=demo:parentFolderAspect
 * owsi.childaspect.demo_parentFolder.childFolderAspect=demo:childFolderAspect
 * owsi.childaspect.demo_parentFolder.childContentAspect=demo:childContentAspect
 */
public class ChildAspectServiceImpl implements ChildAspectService, ApplicationListener<ContextRefreshedEvent> {

	@Autowired private PolicyComponent policyComponent;
	@Autowired private NodeService nodeService;
	@Autowired private DictionaryService dictionaryService;
	@Autowired @Qualifier("NamespaceService") private NamespacePrefixResolver prefixResolver;
	@Autowired @Qualifier("global-properties") private Properties globalProperties;
	
	@Override
	public void onApplicationEvent(ContextRefreshedEvent event) {
		String aspectNamesForFolder = globalProperties.getProperty("owsi.childaspect.register", "");
		for (String aspectName : aspectNamesForFolder.split(",")) {
			if (! aspectName.trim().isEmpty()) {
				String policyKey = aspectName.replace(':', '_');
				
				String childFolderAspect = globalProperties.getProperty("owsi.childaspect." + policyKey + ".childFolderAspect");
				if (childFolderAspect != null) {
					registerChildAspectForFolder(
							QName.createQName(aspectName, prefixResolver),
							QName.createQName(childFolderAspect, prefixResolver));
				}

				String childContentAspect = globalProperties.getProperty("owsi.childaspect." + policyKey + ".childContentAspect");
				if (childContentAspect != null) {
					registerChildAspectForContent(
							QName.createQName(aspectName, prefixResolver),
							QName.createQName(childContentAspect, prefixResolver));
				}
			}
		}
	}

	@Override
	public void registerChildAspectForFolder(QName parentAspect, QName childAspect) {
		registerChildAspectForFolder(parentAspect, new AddAspectConsumer(childAspect));
	}
	@Override
	public void registerChildAspectForContent(QName parentAspect, QName childAspect) {
		registerChildAspectForContent(parentAspect, new AddAspectConsumer(childAspect));
	}
	@Override
	public void registerChildAspectForFolder(QName parentAspect, Consumer<NodeRef> consumer) {
		registerAspectForType(ContentModel.TYPE_FOLDER, parentAspect, consumer);
	}
	@Override
	public void registerChildAspectForContent(QName parentAspect, Consumer<NodeRef> consumer) {
		registerAspectForType(ContentModel.TYPE_CONTENT, parentAspect, consumer);
	}
	
	private class AddAspectConsumer implements Consumer<NodeRef> {
		private QName childAspect;
		public AddAspectConsumer(QName childAspect) {
			this.childAspect = childAspect;
		}
		@Override
		public void accept(NodeRef nodeRef) {
			if (dictionaryService.getType(childAspect) != null) {
				nodeService.setType(nodeRef, childAspect);
			} else {
				nodeService.addAspect(nodeRef, childAspect, null);
			}
		}
	}
	
	public void registerAspectForType(QName type, QName parentAspect, Consumer<NodeRef> consumer) {
		OnCreateChildAssociationPolicy onCreateChildAssociationPolicy = new OnCreateChildAssociationPolicy() {
			@Override
			public void onCreateChildAssociation(ChildAssociationRef childAssocRef, boolean isNewNode) {
				if (   ! nodeService.exists(childAssocRef.getParentRef()) 
					|| ! nodeService.exists(childAssocRef.getParentRef())) {
					return;
				}
				QName childType = nodeService.getType(childAssocRef.getChildRef());
				if (! dictionaryService.isSubClass(childType, type)) {
					return;
				}
				
				consumer.accept(childAssocRef.getChildRef());
			}
		};
		
		policyComponent.bindAssociationBehaviour(OnCreateChildAssociationPolicy.QNAME,
				parentAspect, 
				ContentModel.ASSOC_CONTAINS,
				//new JavaBehaviour(policy, OnCreateChildAssociationPolicy.QNAME.getLocalName(), NotificationFrequency.TRANSACTION_COMMIT));
				new BaseBehaviour(NotificationFrequency.TRANSACTION_COMMIT) {
			@SuppressWarnings("unchecked")
			@Override
			public <T> T getInterface(Class<T> policy) {
				if (policy == OnCreateChildAssociationPolicy.class) {
					return (T) onCreateChildAssociationPolicy;
				}
				return null;
			}
		});
	}
}
