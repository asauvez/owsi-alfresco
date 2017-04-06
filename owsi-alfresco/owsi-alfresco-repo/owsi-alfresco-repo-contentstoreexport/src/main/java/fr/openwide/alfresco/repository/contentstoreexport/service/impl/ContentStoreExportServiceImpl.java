package fr.openwide.alfresco.repository.contentstoreexport.service.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipOutputStream;

import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;

import fr.openwide.alfresco.repository.contentstoreexport.service.ContentStoreExportService;

public class ContentStoreExportServiceImpl implements ContentStoreExportService {

	private NodeService nodeService;
	private SearchService searchService;
	private ContentService contentService;
	
	private String contentstoreexportPaths;
	private String contentstoreexportQueries;
	
	@Override
	public void export(OutputStream outPutStream) throws IOException {
		
		try (ZipOutputStream zipOutPutStream = new ZipOutputStream(outPutStream)) {
			for (NodeRef nodeRef : nodesToExport()) {
				
			}
		}
	}
	
	private Set<NodeRef> nodesToExport () {
		Set<NodeRef> rootNodesToExport = new HashSet<>();
		for (String path : contentstoreexportPaths.split(",")) {
			if (! path.trim().isEmpty()) {
				ResultSet resultSet = searchService.query(
						StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
						SearchService.LANGUAGE_XPATH, path.trim());
				rootNodesToExport.addAll(resultSet.getNodeRefs());
				resultSet.close();
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
		
		return rootNodesToExport;
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
}
