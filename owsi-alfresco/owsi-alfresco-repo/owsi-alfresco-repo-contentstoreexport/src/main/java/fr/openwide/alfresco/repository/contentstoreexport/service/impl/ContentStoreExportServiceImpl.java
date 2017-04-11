package fr.openwide.alfresco.repository.contentstoreexport.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
	public void export(OutputStream outPutStream, String paths, String queries, String noderefs) throws IOException {
		long startTime = System.currentTimeMillis();
		LOGGER.info("lancement de l'export.");
		Properties properties = new Properties();
		Set<NodeRef> rootNodesToExport = getRootNodesToExport(paths, queries, noderefs, properties);
		zipFiles(rootNodesToExport, outPutStream, new HashSet<String>(), properties);
		LOGGER.info("Fin de l'export.");
		long stopTime = System.currentTimeMillis();
		String elapsedTime = secondToTimeConverter(stopTime - startTime);
		properties.setProperty("tempsExecution", elapsedTime);
		System.out.println(properties.toString());
	}

	private Set<NodeRef> getRootNodesToExport(String paths, String queries, String nodeRefs, Properties properties) {
		Set<NodeRef> rootNodesToExport = new HashSet<>();
		properties.setProperty("date", new Timestamp(System.currentTimeMillis()).toString());
		// Paths
		if (paths != null) {
			properties.setProperty("pathsParametresReq", paths);
			for (String path : paths.split(",")) {
				if (!path.trim().isEmpty()) {
					rootNodesToExport.add(getByPath(path));
				}
			}
		}
		properties.setProperty("pathsParametresConfig", contentstoreexportPaths);
		for (String path : contentstoreexportPaths.split(",")) {
			if (!path.trim().isEmpty()) {
				rootNodesToExport.add(getByPath(path));
			}
		}
		// Queries
		if (queries != null) {
			properties.setProperty("queriesParametresReq", queries);
			for (String query : queries.split(",")) {
				if (!query.trim().isEmpty()) {
					ResultSet resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
							SearchService.LANGUAGE_FTS_ALFRESCO, query);
					rootNodesToExport.addAll(resultSet.getNodeRefs());
					resultSet.close();
				}
			}
		}
		properties.setProperty("queriesParametresConfig", contentstoreexportQueries);
		for (String query : contentstoreexportQueries.split(",")) {
			if (!query.trim().isEmpty()) {
				ResultSet resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
						SearchService.LANGUAGE_FTS_ALFRESCO, query);
				rootNodesToExport.addAll(resultSet.getNodeRefs());
				resultSet.close();
			}
		}
		// NodeRefs
		if (nodeRefs != null) {
			properties.setProperty("nodeRefsParametresReq", nodeRefs);
			for (String nodeRef : nodeRefs.split(",")) {
				if (!nodeRef.trim().isEmpty()) {
					rootNodesToExport.add(new NodeRef(nodeRef));
				}
			}
		}

		rootNodesToExport.add(nodeService.getRootNode(new StoreRef("system://system")));
		LOGGER.info("Nombre de racines trouvées: " + rootNodesToExport.size());
		return rootNodesToExport;
	}

	private NodeRef getByPath(String path) {
		NodeRef res = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		for (String childName : path.split("/")) {
			if (!childName.trim().isEmpty()) {
				List<ChildAssociationRef> assocs = nodeService.getChildAssocs(res, RegexQNamePattern.MATCH_ALL,
						QName.createQName(childName, namespacePrefixResolver), true);
				if (assocs.size() != 1) {
					throw new IllegalArgumentException(path + " : " + assocs.size());
				}
				res = assocs.get(0).getChildRef();
			}
		}
		return res;
	}

	private int zipFiles(Set<NodeRef> rootList, OutputStream outPutStream, Set<String> processedNodes, Properties properties)
			throws IOException {
		int count = 0;
		try (ZipOutputStream zipOutPutStream = new ZipOutputStream(outPutStream)) {
			for (NodeRef root : rootList) {
				count += recurseThroughNodeRefChilds(root, zipOutPutStream, processedNodes);
			}
		}
		LOGGER.info("Nombre de fichiers exportés: " + count);
		properties.setProperty("nbrFichiersExportes", "" + count);
		return count;
	}

	private int recurseThroughNodeRefChilds(NodeRef nodeRef, ZipOutputStream zip, Set<String> processedNodes)
			throws IOException {
		int count = 0;
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		for (Entry<QName, Serializable> property : properties.entrySet()) {
			if (property.getValue() instanceof ContentData) {
				ContentData contentData = (ContentData) property.getValue();
				ContentReader reader = contentService.getReader(nodeRef, property.getKey());
				try (InputStream in = reader.getContentInputStream()) {
					String contentUrl = contentData.getContentUrl();
					if (processedNodes.add(contentUrl)) {
						/*if (contentUrl.startsWith(STORE_PREFIX)) {
							contentUrl = contentUrl.substring(STORE_PREFIX.length());
						}*/
						zip.putNextEntry(new ZipEntry(contentUrl));
						IOUtils.copy(in, zip);
						count++;
					}
				}
				zip.closeEntry();
			}
		}
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		if (children.size() > 0) {
			for (ChildAssociationRef childAssoc : children) {
				NodeRef childNodeRef = childAssoc.getChildRef();
				count += recurseThroughNodeRefChilds(childNodeRef, zip, processedNodes);
			}
		}
		return count;
	}

	private String secondToTimeConverter (long seconds) {
		long s = seconds %60;
		long m = (seconds / 60) % 60;
		long h = (seconds / (60 * 60)) % 24;
		return String.format("%d:%02d:%02d", h,m,s);
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
