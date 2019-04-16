package org.owsi.alfresco.repo.datalistgroupe.policies;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.model.DataListModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.policy.Behaviour;
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

public class DatalistAuthorityListener implements NodeServicePolicies.OnCreateNodePolicy, NodeServicePolicies.BeforeDeleteNodePolicy, 
													NodeServicePolicies.OnUpdatePropertiesPolicy {
	
	private final Logger LOGGER = LoggerFactory.getLogger(DatalistAuthorityListener.class);
	// Dependencies
	private NodeService nodeService;
	private PolicyComponent policyComponent;
	private AuthorityService authorityService;
	private SiteService siteService;
	private NamespaceService namespaceService;
	private String prefix, suffix; 
	
	
	private static final String SITE_NAME_REPLACEMENT_STRING = "[NOM_DU_SITE]";
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}

	public void setPolicyComponent(PolicyComponent policyComponent) {
		this.policyComponent = policyComponent;
	}
	
	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
		}

	public void setSiteService(SiteService siteService) {
		this.siteService = siteService;
	}
	

	public void setNamespaceService(NamespaceService namespaceService) {
		this.namespaceService = namespaceService;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	// Behaviours
	private Behaviour onCreateNode;
	private Behaviour beforeDeleteNode;
	private Behaviour onUpdateProperties;
	
	public void init() {

		// Create behaviours
		this.onCreateNode = new JavaBehaviour(this, "onCreateNode", NotificationFrequency.TRANSACTION_COMMIT);
		this.beforeDeleteNode = new JavaBehaviour(this, "beforeDeleteNode", NotificationFrequency.FIRST_EVENT);
		this.onUpdateProperties = new JavaBehaviour(this, "onUpdateProperties", NotificationFrequency.TRANSACTION_COMMIT);
		
		// Bind behaviours to node policies
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.OnCreateNodePolicy.QNAME,
				DataListModel.TYPE_DATALIST,
				this.onCreateNode
				);
		
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.BeforeDeleteNodePolicy.QNAME,
				DataListModel.TYPE_DATALIST,
				this.beforeDeleteNode
				);
		
		this.policyComponent.bindClassBehaviour(
				NodeServicePolicies.OnUpdatePropertiesPolicy.QNAME,
				DataListModel.TYPE_DATALIST,
				this.onUpdateProperties
				);
	}
	
	@Override
	public void onCreateNode(ChildAssociationRef childAssocRef) {
		
		final NodeRef finalNodeRef = childAssocRef.getChildRef();
		AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>() {
			@Override
			public Object doWork() throws Exception {
				if(nodeService.exists(finalNodeRef)) {
					if(isAuthorityDatalist(finalNodeRef)) {
						// create the group
						String authorityShortName = getGroupName(finalNodeRef);
						String authorityName = authorityService.getName(AuthorityType.GROUP, authorityShortName);
						if (authorityService.authorityExists(authorityName)) {
							throw new IllegalArgumentException(authorityShortName + " existe déjà !");
						} else {
							authorityService.createAuthority(AuthorityType.GROUP, authorityShortName);
							//Add aspect "dlauthority:group"
							nodeService.addAspect(finalNodeRef, DatalistAuthorityModel.ASPECT_DATALISTAUTHORITY_GROUP, null);
							nodeService.setProperty(finalNodeRef, DatalistAuthorityModel.PROP_DATALISTAUTHORITY_GROUP_NAME,
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
	
	private String getGroupName(NodeRef nodeRef) {
		SiteInfo siteInfo = siteService.getSite(nodeRef);
		String name = prefix + nodeService.getProperty(nodeRef, ContentModel.PROP_TITLE) + suffix;
		// On remplace la constante des préfixes/suffixes correspondant au nom du site
		return name.replaceAll(SITE_NAME_REPLACEMENT_STRING, siteInfo.getShortName());
	}
}
