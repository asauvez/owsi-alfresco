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
		
			String TYPE_DATALISTAUTHORITY_ITEM = qnameToString(DatalistAuthorityModel.TYPE_DATALISTAUTHORITY_ITEM, true);
			if((nodeService.getProperty(nodeRef, DataListModel.PROP_DATALIST_ITEM_TYPE)!= null) && 
					TYPE_DATALISTAUTHORITY_ITEM.equals(nodeService.getProperty(nodeRef, DataListModel.PROP_DATALIST_ITEM_TYPE))) {
				return true;
			}
		return false;
	}
	
	private String getGroupName(NodeRef nodeRef) {
		final SiteInfo siteInfo = siteService.getSite(nodeRef);
		String name = prefix + (String)nodeService.getProperty(nodeRef, ContentModel.PROP_TITLE) + suffix;
		return name.replaceAll("NOM_DU_SITE", siteInfo.getShortName());
	}
	
	// Thread local cache of namespace prefixes for long QName to short prefix name conversions
	protected static ThreadLocal<Map<String, String>> namespacePrefixCache = new ThreadLocal<Map<String, String>>() {
		@Override
		protected Map<String, String> initialValue()
		{
			return new HashMap<String, String>(8);
		}
	};

	private String qnameToString(final QName qname, final boolean isShortName) {
	String result;
		if (isShortName) {
			final Map<String, String> cache = namespacePrefixCache.get();
			String prefix = cache.get(qname.getNamespaceURI());
			if (prefix == null) {
				// first request for this namespace prefix, get and cache result
				Collection<String> prefixes = this.namespaceService.getPrefixes(qname.getNamespaceURI());
				prefix = prefixes.size() != 0 ? prefixes.iterator().next() : "";
				cache.put(qname.getNamespaceURI(), prefix);
			}
			result = prefix + QName.NAMESPACE_PREFIX + qname.getLocalName();
		}
		else {
			result = qname.toString();
		}
		return result;
	}
}
