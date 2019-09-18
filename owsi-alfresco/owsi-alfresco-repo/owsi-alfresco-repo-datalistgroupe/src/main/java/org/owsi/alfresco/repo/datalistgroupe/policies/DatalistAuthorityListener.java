package org.owsi.alfresco.repo.datalistgroupe.policies;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.model.DataListModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.owsi.alfresco.repo.datalistgroupe.model.DatalistAuthorityModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

public class DatalistAuthorityListener implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy, 
													NodeServicePolicies.OnUpdatePropertiesPolicy {
	
	private final Logger LOGGER = LoggerFactory.getLogger(DatalistAuthorityListener.class);
	
	// Dependencies
	@Autowired private NodeService nodeService;
	@Autowired private PolicyComponent policyComponent;
	@Autowired private AuthorityService authorityService;
	@Autowired private SiteService siteService;
	@Autowired private NamespaceService namespaceService;

	@Value("${datalistegroupe.nom_groupe.pattern}")
	private  String pattern;
	
	public void init() {
		
		// Bind behaviours to node policies
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DataListModel.TYPE_DATALIST,
				new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT));
		
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DataListModel.TYPE_DATALIST,
				new JavaBehaviour(this, "beforeDeleteNode", NotificationFrequency.FIRST_EVENT));
		
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DataListModel.TYPE_DATALIST,
				new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT));
	}
	
	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		
		final NodeRef groupNodeRef = childAssocRef.getChildRef();
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				if(nodeService.exists(groupNodeRef)) {
					if(isAuthorityDatalist(groupNodeRef)) {
						// create the group
						
						String groupName = (String) nodeService.getProperty(groupNodeRef, ContentModel.PROP_TITLE);
						SiteInfo siteInfo = siteService.getSite(groupNodeRef);
						String authorityShortName = MessageFormat.format(pattern, siteInfo.getShortName(), groupName);

						String authorityName = authorityService.getName(AuthorityType.GROUP, authorityShortName);
						if (authorityService.authorityExists(authorityName)) {
							throw new IllegalArgumentException(authorityShortName + " existe déjà !");
						} else {
							authorityService.createAuthority(AuthorityType.GROUP, authorityShortName);
							
							// Les membres de ce site font automatiquement parti du site
							String siteGroup = siteService.getSiteGroup(siteInfo.getShortName());
							authorityService.addAuthority(siteGroup, authorityShortName);
							
							//Add aspect "dlauthority:group"
							nodeService.addAspect(groupNodeRef, DatalistAuthorityModel.ASPECT_DATALISTAUTHORITY_GROUP, null);
							nodeService.setProperty(groupNodeRef, DatalistAuthorityModel.PROP_DATALISTAUTHORITY_GROUP_NAME,
									authorityName);
						}
					}
				}
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());
	}

	@Override
	public void beforeDeleteNode(NodeRef nodeRef) {
		
		final NodeRef finalNodeRef = nodeRef;
		
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			
			@Override
			public Object doWork() throws Exception {
				if(nodeService.exists(finalNodeRef)) {
					if(isAuthorityDatalist(finalNodeRef)) {
						String authorityName = (String) nodeService.getProperty(finalNodeRef, DatalistAuthorityModel.PROP_DATALISTAUTHORITY_GROUP_NAME);
						if (! authorityService.authorityExists(authorityName)) {
							LOGGER.warn(authorityName + " n'existe pas !");
						} else {
							authorityService.deleteAuthority(authorityName);
						}
					}

				}
				return null;
			}
			
		}, AuthenticationUtil.getSystemUserName());
	}

	@Override
	public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
		final NodeRef finalNodeRef = nodeRef;
		final Map<QName, Serializable> propertiesBefore = before;
		final Map<QName, Serializable> propertiesAfter = after;
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				if(nodeService.exists(finalNodeRef)) {
					if(isAuthorityDatalist(finalNodeRef)) {
						if ((! propertiesBefore.isEmpty()) && 
								(! propertiesAfter.get(ContentModel.PROP_TITLE).equals(propertiesBefore.get(ContentModel.PROP_TITLE)))) {
							throw new IllegalArgumentException("La mise à jour du nom de la datalist est interdit");
						}
					}
				}
				return null;
			}
		}, AuthenticationUtil.getSystemUserName());
	}
	
	private boolean isAuthorityDatalist(NodeRef nodeRef) {
		String datalistType = (String) nodeService.getProperty(nodeRef, DataListModel.PROP_DATALIST_ITEM_TYPE);
		return datalistType != null && datalistType.equals(DatalistAuthorityModel.TYPE_DATALISTAUTHORITY_ITEM.toPrefixString(namespaceService));
	}
}
