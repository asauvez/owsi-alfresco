package fr.openwide.alfresco.repository.contentstoreexport.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.alfresco.model.ContentModel;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.repository.contentstoreexport.service.ContentStoreExportService;

public class ContentStoreExportServiceImpl implements ContentStoreExportService {

	private static final String STORE_PREFIX = "store:/";

	private final Logger LOGGER = LoggerFactory.getLogger(ContentStoreExportServiceImpl.class);
	
	private NodeService nodeService;
	private SearchService searchService;
	private ContentService contentService;
	private NamespacePrefixResolver namespacePrefixResolver;
	
	private String contentstoreexportPaths;
	private String contentstoreexportQueries;
	
	@Override
	public void export(OutputStream outPutStream) throws IOException {
		LOGGER.info(": lancement de l'export.");
		zipFiles(getRootNodesToExport(), outPutStream);
		LOGGER.info(": Fin de l'export.");
	}
	
	private Set<NodeRef> getRootNodesToExport() {
		Set<NodeRef> rootNodesToExport = new HashSet<>();
		for (String path : contentstoreexportPaths.split(",")) {
			if (! path.trim().isEmpty()) {
				rootNodesToExport.add(getByPath(path));
			}
		}
		for (String path : contentstoreexportQueries.split(",")) {
			if (! path.trim().isEmpty()) {
				ResultSet resultSet = searchService.query(
						StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
						SearchService.LANGUAGE_FTS_ALFRESCO, path);
				rootNodesToExport.addAll(resultSet.getNodeRefs());
				resultSet.close();
			}
		}
		LOGGER.info(": Nombre de racines trouvées: " + rootNodesToExport.size());
		return rootNodesToExport;
	}
	
	private NodeRef getByPath(String path) {
		NodeRef res = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		for (String childName : path.split("/")) {
			if (! childName.trim().isEmpty()) {
				List<ChildAssociationRef> assocs = nodeService.getChildAssocs(res, RegexQNamePattern.MATCH_ALL, QName.createQName(childName, namespacePrefixResolver), true);
				if (assocs.size() != 1) {
					throw new IllegalArgumentException(path + " : " + assocs.size());
				}
				res = assocs.get(0).getChildRef();
			}
		}
		
		return res;
	}
	
	private void zipFiles(Set<NodeRef> rootList, OutputStream outPutStream) throws IOException {
		try (ZipOutputStream zipOutPutStream = new ZipOutputStream(outPutStream)) {
			for (NodeRef root : rootList) {
				recurseThroughNodeRefChilds(root, zipOutPutStream);
			}
		}
	}
	
	private void recurseThroughNodeRefChilds(NodeRef nodeRef, ZipOutputStream zip) throws IOException {
		ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
		if (contentData != null) {
			ContentReader reader = contentService.getReader(nodeRef, ContentModel.PROP_CONTENT);
			try (InputStream in = reader.getContentInputStream()) {
				String contentUrl = contentData.getContentUrl();
				if (contentUrl.startsWith(STORE_PREFIX)) {
					contentUrl = contentUrl.substring(STORE_PREFIX.length());
				}
				zip.putNextEntry(new ZipEntry(contentUrl));
				IOUtils.copy(in, zip);
			}
			zip.closeEntry();
		}
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		if (children.size()>0) {
			for (ChildAssociationRef childAssoc : children) {
				NodeRef childNodeRef = childAssoc.getChildRef();
				recurseThroughNodeRefChilds(childNodeRef, zip);
			}
		}
	}
	
	public void setNodeService(NodeService nodeService) {
		this.nodeService = nodeService;
	}
	public void setContentstoreexportPaths(String contentstoreexportPaths) {
		this.contentstoreexportPaths = contentstoreexportPaths;
	}
	public void setSearchService(SearchService searchService) {
		this.searchService = searchService;
	}
	public void setContentstoreexportQueries(String contentstoreexportQueries) {
		this.contentstoreexportQueries = contentstoreexportQueries;
	}
	public void setContentService(ContentService contentService) {
		this.contentService = contentService;
	}
	public void setNamespacePrefixResolver(NamespacePrefixResolver namespacePrefixResolver) {
		this.namespacePrefixResolver = namespacePrefixResolver;
	}
}
