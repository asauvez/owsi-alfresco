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
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
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
	@Autowired private NodeService nodeService;
	
	@Autowired @Qualifier("global-properties")
	private Properties globalProperties;

	@Override
	public void generateAudit(PrintWriter out) {
		// owsi.solraudit.pivot=cm:creator,SITE,cm:content.mimetype
		List<String> pivots = new ArrayList<>(Arrays.asList(
				globalProperties.getProperty("owsi.solraudit.pivot", "SITE").split(",")));
		String query = globalProperties.getProperty("owsi.solraudit.query", "TYPE:\"" + ContentModel.TYPE_CONTENT + "\"");
		
		// Ajout header CSV
		for (String pivot : pivots) {
			if (pivot.contains(":")) {
				pivot = pivot.substring(pivot.indexOf(":")+1);
			}
			out.append(pivot);
			out.append(";");
		}
		out.append("Nombre;Taille\n");

		pivots.add(0, PIVOT_LABEL);
		SearchParameters params = new SearchParameters();
		params.addStore(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
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
		
		manageFacets(out, "", pivots, rset.getPivotFacets(), new HashMap<String, String>());
	}

	private void manageFacets(PrintWriter out, String prefix, List<String> pivots, List<GenericFacetResponse> facets, Map<String, String> values) {
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
					}
					out.append(count + ";" + sum + "\n");
				} else {
					manageFacets(out, prefix + "    ", pivots, bucket.getFacets(), values);
				}
				values.remove(facet.getLabel());
			}
		}
	}
	
	@Override
	public void storeAudit( ) {
		String pathElements = globalProperties.getProperty("owsi.solraudit.storePath", "solrAudit");
		String fileName = globalProperties.getProperty("owsi.solraudit.storeFileName", "solrAudit_{0,date,yyyy-MM-dd}.csv");
		fileName = MessageFormat.format(fileName, new Date());
		
		NodeRef folder = repositoryHelper.getCompanyHome();
		for (String pathElement : pathElements.split("/")) {
			if (! pathElement.trim().isEmpty()) {
				NodeRef child = nodeService.getChildByName(folder, ContentModel.ASSOC_CONTAINS, pathElement.trim());
				if (child != null) {
					folder = child;
				} else {
					folder = fileFolderService.create(folder, pathElement.trim(), ContentModel.TYPE_FOLDER).getNodeRef();
				}
			}
		}
		
		NodeRef file = nodeService.getChildByName(folder, ContentModel.ASSOC_CONTAINS, fileName);
		if (file == null) {
			file = fileFolderService.create(folder, fileName, ContentModel.TYPE_CONTENT).getNodeRef();
		} else {
			logger.warn("File already exists. Override it : " + pathElements + " / " + fileName);
		}
		
		ContentWriter writer = fileFolderService.getWriter(file);
		try (PrintWriter out = new PrintWriter(new OutputStreamWriter(writer.getContentOutputStream(), "UTF-8"))) {
			generateAudit(out);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
