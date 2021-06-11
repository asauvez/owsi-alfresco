package fr.openwide.alfresco.repo.solraudit.service.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.search.impl.lucene.SolrJSONResultSet;
import org.alfresco.repo.search.impl.solr.facet.facetsresponse.GenericBucket;
import org.alfresco.repo.search.impl.solr.facet.facetsresponse.GenericFacetResponse;
import org.alfresco.repo.search.impl.solr.facet.facetsresponse.Metric;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.search.StatsRequestParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

/**
 * Génére un fichier CSV avec le nombre et la taille des fichiers selons des critères spécifiques.
 */
@GenerateService
public class SolrAuditServiceImpl implements SolrAuditService {

	private static final String PIVOT_LABEL = "piv1";

	private static final Logger logger = LoggerFactory.getLogger(SolrAuditServiceImpl.class);

	@Autowired private SearchService searchService;
	@Autowired private FileFolderService fileFolderService;
	@Autowired @Qualifier("repositoryHelper") private Repository repositoryHelper;
	@Autowired private MimetypeService mimetypeService;
	
	@Autowired @Qualifier("global-properties")
	private Properties globalProperties;

	@Override
	public void generateAudit(PrintWriter out) {
		generateAuditInternal(out, StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, false);
	}
	@Override
	public void generateAudit(PrintWriter out, StoreRef storeRef) {
		generateAuditInternal(out, storeRef, false);
	}

	@Override
	public void storeAudit() {
		storeAudit(false);
	}
	@Override
	public void storeAudit(boolean includeTrashcan) {
		String pathElements = globalProperties.getProperty("owsi.solraudit.storePath", "solrAudit");
		String fileName = globalProperties.getProperty("owsi.solraudit.storeFileName", "solrAudit_{0,date,yyyy-MM-dd}.csv");
		fileName = MessageFormat.format(fileName, new Date());
		
		NodeRef folder = repositoryHelper.getCompanyHome();
		for (String pathElement : pathElements.split("/")) {
			if (! pathElement.trim().isEmpty()) {
				NodeRef child = fileFolderService.searchSimple(folder, pathElement.trim());
				if (child != null) {
					folder = child;
				} else {
					folder = fileFolderService.create(folder, pathElement.trim(), ContentModel.TYPE_FOLDER).getNodeRef();
				}
			}
		}
		
		NodeRef file = fileFolderService.searchSimple(folder, fileName);
		if (file == null) {
			file = fileFolderService.create(folder, fileName, ContentModel.TYPE_CONTENT).getNodeRef();
		} else {
			logger.warn("File already exists. Override it : " + pathElements + " / " + fileName);
		}
		
		ContentWriter writer = fileFolderService.getWriter(file);
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(writer.getContentOutputStream(), "ISO-8859-1"))) {
			generateAuditInternal(out, StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, true);
			if (includeTrashcan) {
				generateAuditInternal(out, StoreRef.STORE_REF_ARCHIVE_SPACESSTORE, false);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private void generateAuditInternal(PrintWriter out, StoreRef storeRef, boolean includeHeaders) {
		logger.debug("Generate audit for storeRef " + storeRef.getProtocol());
		// owsi.solraudit.pivot=cm:creator,SITE,cm:content.mimetype
		List<String> pivots = new ArrayList<>(Arrays.asList(
				globalProperties.getProperty("owsi.solraudit.pivot", "SITE").split(",")));
		String query = globalProperties.getProperty("owsi.solraudit.query", "TYPE:\"" + ContentModel.TYPE_CONTENT + "\"");
		
		// Ajout header CSV
		if (includeHeaders) {
			for (String pivot : pivots) {
				if (pivot.contains(":")) {
					pivot = pivot.substring(pivot.indexOf(":")+1);
				}
				out.append(pivot);
				out.append(";");
				if (isPivotMimeType(pivot)) {
					out.append(pivot + "_Display;");
				}
			}
			out.append("Corbeille;Nombre;Taille\n");
		}

		pivots.add(0, PIVOT_LABEL);
		SearchParameters params = new SearchParameters();
		params.addStore(storeRef);
		params.setLanguage("fts-alfresco");
		params.setQuery(query);
		params.addPivots(pivots);
		params.setMaxItems(0);
		params.setStats(Arrays.asList(new StatsRequestParameters(
				"cm:content.size", 
				PIVOT_LABEL, 
				null, null, null, 
				true, true, // sum et count
				null, null, null, null, null, null, null, null, null)));
		
		SolrJSONResultSet rset = (SolrJSONResultSet) searchService.query(params);
		pivots.remove(0);
		
		manageFacets(out, "", pivots, rset.getPivotFacets(), new HashMap<String, String>(), StoreRef.STORE_REF_ARCHIVE_SPACESSTORE.equals(storeRef));
	}

	private void manageFacets(PrintWriter out, String prefix, List<String> pivots, List<GenericFacetResponse> facets, 
			Map<String, String> values, boolean isTrashCan) {
		for (GenericFacetResponse facet : facets) {
			logger.debug(prefix + facet.getLabel()); // cm:content.mimetype
			
			for (GenericBucket bucket : facet.getBuckets()) {
				Integer count = null;
				Long sum = null;
				for (Metric metric : bucket.getMetrics())  {
					switch (metric.getType()) {
					case count: 
						Object object = metric.getValue().get("count");
						count = (object instanceof String) ? Integer.parseInt((String) object) : ((Number) object).intValue();
						break;
					case sum: sum = ((Number) metric.getValue().get("sum")).longValue(); break;
					default: break;
					}
				}
				logger.debug(prefix + "- " + bucket.getLabel() + "/ " + count + " / " + sum); // text/plain
				
				values.put(facet.getLabel(), bucket.getLabel());
				if (bucket.getFacets().isEmpty()) {
					for (String pivot : pivots) {
						String value = values.get(pivot);
						out.append((value != null) ? value : "");
						out.append(";");
						if (isPivotMimeType(pivot)) {
							if (value == null) {
								out.append(";");
							}
							else {
								Map<String, String> displaysByMimetype = mimetypeService.getDisplaysByMimetype();
								String valueDisplay = displaysByMimetype.getOrDefault(value, value);
								out.append((valueDisplay != null) ? valueDisplay : "");
								out.append(";");
							}
						}
					}
					out.append(isTrashCan + ";" + count + ";" + sum + "\n");
				} else {
					manageFacets(out, prefix + "    ", pivots, bucket.getFacets(), values, isTrashCan);
				}
				values.remove(facet.getLabel());
			}
		}
	}
	
	private boolean isPivotMimeType(String pivot) {
		return pivot.endsWith(".mimetype");
	}
}
