package fr.openwide.alfresco.repo.core.node.service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.model.DataListModel;
import org.alfresco.repo.admin.SysAdminParams;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.Path.ChildAssocElement;
import org.alfresco.service.cmr.repository.Path.Element;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.webdav.WebDavService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.UrlUtil;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.repo.core.configurationlogger.AlfrescoGlobalProperties;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService
public class AlfrescoUrlService {
	
	@SuppressWarnings("serial")
	private Map<String, String> msProtocoleNames = new HashMap<String, String>() {{
		put("doc", "ms-word");
		put("docx", "ms-word");
		put("docm", "ms-word");
		put("dot", "ms-word");
		put("dotx", "ms-word");
		put("dotm", "ms-word");
		put("xls", "ms-excel");
		put("xlsx", "ms-excel");
		put("xlsb", "ms-excel");
		put("xlsm", "ms-excel");
		put("xlt", "ms-excel");
		put("xltx", "ms-excel");
		put("xltm", "ms-excel");
		put("xlsm", "ms-excel");
		put("ppt", "ms-powerpoint");
		put("pptx", "ms-powerpoint");
		put("pot", "ms-powerpoint");
		put("potx", "ms-powerpoint");
		put("potm", "ms-powerpoint");
		put("pptm", "ms-powerpoint");
		put("potm", "ms-powerpoint");
		put("pps", "ms-powerpoint");
		put("ppsx", "ms-powerpoint");
		put("ppam", "ms-powerpoint");
		put("ppsm", "ms-powerpoint");
		put("sldx", "ms-powerpoint");
		put("sldm", "ms-powerpoint");
	}};

	@Autowired private WebDavService webDavService;
	@Autowired private NodeService nodeService;
	@Autowired private SysAdminParams sysAdminParams;
	@Autowired private SiteService siteService;
	@Autowired private AlfrescoGlobalProperties alfrescoGlobalProperties;
	@Autowired private FileFolderService fileFolderService;
	
	/** Inspired by: https://github.com/Alfresco/share/blob/6.0/share/src/main/webapp/components/documentlibrary/actions.js */
	public String getShareOnlineEditionUrl(NodeRef nodeRef) {
		String originalNameDocument = (String) nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		String msType = msProtocoleNames.get(FilenameUtils.getExtension(originalNameDocument).toLowerCase());
		
		String finalurl;
		if (msType == null) {
			// No editor for this type. Return download link
			//finalurl = UrlUtil.getAlfrescoUrl(sysAdminParams) + "/api/-default-/public/alfresco/versions/1/nodes/" + uuid + "/content";
			finalurl = UrlUtil.getAlfrescoUrl(sysAdminParams) + "/s/api/node/" 
					+ nodeRef.getStoreRef().getProtocol() + "/" + nodeRef.getStoreRef().getIdentifier() + "/" 
					+ nodeRef.getId() + "/content";
		} else {
			StringBuilder buf = new StringBuilder()
					.append(msType)
					.append(":ofe|u|")
					.append(UrlUtil.getAlfrescoUrl(sysAdminParams))
					.append("/aos");
			
			Iterator<Element> it = nodeService.getPath(nodeRef).iterator();
			it.next(); // ignore "/"
			it.next(); // ignore company_home
			while (it.hasNext()) {
				ChildAssociationRef assoc = ((ChildAssocElement) it.next()).getRef();
				String folderName = (String) nodeService.getProperty(assoc.getParentRef(), ContentModel.PROP_NAME);
				buf.append("/").append(encodeUrl(folderName));
			}
			finalurl = buf.toString();
		}
		return finalurl;
	}
	
	public String getWebDavUrl(NodeRef nodeRef) {
		return UrlUtil.getAlfrescoUrl(sysAdminParams) + webDavService.getWebdavUrl(nodeRef);
	}
	public String getShareViewUrl(NodeRef nodeRef) {
		SiteInfo site = siteService.getSite(nodeRef);
		
		FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);
		if (fileInfo == null) {
			throw new IllegalStateException("Node does not exist " + nodeRef);
		}
		if (fileInfo.isFolder()) {
			StringBuilder path = new StringBuilder();
			
			Iterator<Element> it = nodeService.getPath(nodeRef).iterator();
			it.next(); // ignore "/"
			it.next(); // ignore company_home
			if (site != null) {
				it.next(); // ignore "sites"
				it.next(); // ignore nom du site
				if (! it.hasNext()) {
					return UrlUtil.getShareUrl(sysAdminParams) + "/page/site/" + site.getShortName() + "/dashboard";
				}
				it.next(); // ignore documentlibrary
			}
			while (it.hasNext()) {
				ChildAssociationRef assoc = ((ChildAssocElement) it.next()).getRef();
				String folderName = (String) nodeService.getProperty(assoc.getChildRef(), ContentModel.PROP_NAME);
				path.append("/").append(encodeUrl(folderName));
			}

			return UrlUtil.getShareUrl(sysAdminParams) + "/page/" 
				+ ((site != null) 
						? "site/" + site.getShortName() + "/documentlibrary"
						: "repository")
				+ ((path.length() > 0) ? "#filter=path%7C" + encodeUrl(path.toString()) + "%7C" : "");
			
		} else if (nodeService.getType(nodeRef).equals(DataListModel.TYPE_DATALIST)) {
			return UrlUtil.getShareUrl(sysAdminParams) + "/page/" 
					+ ((site != null) ? "site/" + site.getShortName() : "")
					+ "/data-lists?list=" + nodeRef.getId();
		} else {
			return UrlUtil.getShareUrl(sysAdminParams) + "/page/" 
				+ ((site != null) ? "site/" + site.getShortName() : "")
				+ "/document-details?nodeRef=" + nodeRef;
		}
	}
	
	private String encodeUrl(String s) {
		try {
			return URLEncoder.encode(s, "UTF-8").replace("+", "%20");
		} catch (UnsupportedEncodingException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public String getDownloadUrl(NodeRef nodeRef, QName property) {
		// TODO manage properties other than cm:content
		return UrlUtil.getAlfrescoUrl(sysAdminParams) + "/s/api/node/" 
			+ nodeRef.getStoreRef().getProtocol() + "/" + nodeRef.getStoreRef().getIdentifier() + "/" 
			+ nodeRef.getId() + "/content";
			//+ ((rendition != null) ? "/thumbnails/" + rendition : "");
	}
	
	public String getContentAppUrl() {
		StringBuilder url = new StringBuilder();
		String protocol = alfrescoGlobalProperties.getProperty("contentApp.protocol", alfrescoGlobalProperties.getProperty("alfresco.protocol", "http"));
		int port = alfrescoGlobalProperties.getPropertyInt("contentApp.port", alfrescoGlobalProperties.getPropertyInt("alfresco.port", 8080));
		
		url.append(protocol);
		url.append("://");
		url.append(alfrescoGlobalProperties.getProperty("contentApp.host", alfrescoGlobalProperties.getProperty("alfresco.host", "localhost")));
		if ("http".equals(protocol) && port == 80) {
			// Not needed
		} else if ("https".equals(protocol) && port == 443) {
			// Not needed
		} else {
			url.append(':');
			url.append(port);
		}
		String context = alfrescoGlobalProperties.getProperty("contentApp.context", "content-app");
		if (StringUtils.isNotEmpty(context)) {
			url.append('/');
			url.append(context);
		}
		return url.toString();
	}
	
	public String getContentAppViewUrl(NodeRef nodeRef) {
		FileInfo fileInfo = fileFolderService.getFileInfo(nodeRef);
		if (fileInfo == null) {
			throw new IllegalStateException("Node does not exist " + nodeRef);
		}
		if (fileInfo.isFolder()) {
			return getContentAppUrl() + "/libraries/" + nodeRef.getId();
		} else {
			ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
			return getContentAppUrl() + "/libraries/" + primaryParent.getParentRef().getId()
					+ "/preview/" + nodeRef.getId();
		}
	}
}