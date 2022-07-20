package fr.openwide.alfresco.repo.solraudit.service.impl;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Consumer;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.BaseBehaviour;
import org.alfresco.repo.policy.Behaviour.NotificationFrequency;
import org.alfresco.repo.policy.BehaviourFilter;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.repo.search.impl.solr.SolrJSONResultSet;
import org.alfresco.repo.search.impl.solr.facet.facetsresponse.GenericBucket;
import org.alfresco.repo.search.impl.solr.facet.facetsresponse.GenericFacetResponse;
import org.alfresco.repo.search.impl.solr.facet.facetsresponse.Metric;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchParameters;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.search.StatsRequestParameters;
import org.alfresco.service.namespace.QName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.openwide.alfresco.repo.solraudit.service.SolrAuditService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

/**
 * Génére un fichier CSV avec le nombre et la taille des fichiers selons des critères spécifiques.
 */
@GenerateService
public class SolrAuditServiceImpl implements SolrAuditService, InitializingBean {

	private static final String PIVOT_LABEL = "piv1";

	private static final Logger logger = LoggerFactory.getLogger(SolrAuditServiceImpl.class);

	@Autowired private NodeService nodeService;
	@Autowired private SearchService searchService;
	@Autowired private FileFolderService fileFolderService;
	@Autowired @Qualifier("repositoryHelper") private Repository repositoryHelper;
	@Autowired private MimetypeService mimetypeService;
	@Autowired private PolicyComponent policyComponent;
	@Autowired
	@Qualifier("policyBehaviourFilter")
	private BehaviourFilter behaviourFilter;

	@Autowired @Qualifier("global-properties")
	private Properties globalProperties;

	private String pathElements;
	private String fileNamePattern;
	private List<String> pivots;
	private String query;

	@Override
	public void afterPropertiesSet() throws Exception {
		pathElements = globalProperties.getProperty("owsi.solraudit.storePath", "solrAudit");
		fileNamePattern = globalProperties.getProperty("owsi.solraudit.storeFileName", "solrAudit_{0,date,yyyy-MM-dd}.csv");
		pivots = new ArrayList<>(Arrays.asList(
				globalProperties.getProperty("owsi.solraudit.pivot", "SITE,cm:content.mimetype").split(",")));
		query = globalProperties.getProperty("owsi.solraudit.query", "TYPE:\"" + ContentModel.TYPE_CONTENT + "\"");
	}
	
	@Override
	public void generateAudit(PrintWriter out) {
		generateAuditInternal(out, StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, true);
		generateAuditInternal(out, StoreRef.STORE_REF_ARCHIVE_SPACESSTORE, false);
	}
	@Override
	public void generateAudit(PrintWriter out, StoreRef storeRef) {
		generateAuditInternal(out, storeRef, true);
	}

	@Override
	public void storeAudit() {
		storeAudit(false);
	}
	@Override
	public void storeAudit(boolean includeTrashcan) {
		String fileName = MessageFormat.format(fileNamePattern, new Date());
		
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
	
	@Override
	public void registerPropertiesPolicy(QName container, Consumer<NodeRef> consumer) {
		OnUpdatePropertiesPolicy onUpdatePropertiesPolicy = new OnUpdatePropertiesPolicy() {
			@Override
			public void onUpdateProperties(NodeRef nodeRef, Map<QName, Serializable> before, Map<QName, Serializable> after) {
				if (! nodeService.exists(nodeRef)) {
					return;
				}
				
				behaviourFilter.disableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
				try {
					consumer.accept(nodeRef);
				} finally {
					behaviourFilter.enableBehaviour(nodeRef, ContentModel.ASPECT_AUDITABLE);
				}
			}
		};
		
		policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME, container, 
			new BaseBehaviour(NotificationFrequency.TRANSACTION_COMMIT) {
				@SuppressWarnings("unchecked")
				@Override
				public <T> T getInterface(Class<T> policy) {
					if (policy == OnUpdatePropertiesPolicy.class) {
						return (T) onUpdatePropertiesPolicy;
					}
					return null;
				}
			});
	}
	
	@Override
	public void setQuery(String query) {
		this.query = query;
	}
	@Override
	public void addPivots(QName... properties) {
		for (QName property : properties) {
			pivots.add(property.toPrefixString());
		}
	}
	
	@Override
	public void registerDateGroup(QName container, QName propertyText) {
		registerDateGroup(container, ContentModel.PROP_CREATED, propertyText, "yyyy/MM");
	}
	@Override
	public void registerDateGroup(QName container, QName propertyDate, QName propertyText, String format) {
		registerPropertiesPolicy(container, (nodeRef) -> {
			Date date = (Date) nodeService.getProperty(nodeRef, propertyDate);
			String dateGroup = (date != null) ? new SimpleDateFormat(format).format(date) : null;
			nodeService.setProperty(nodeRef, propertyText, dateGroup);
		});
	}
	
	@Override
	public void registerLogSize(QName container, QName propertyText) {
		registerPropertiesPolicy(container, (nodeRef) -> {
			// Stock une valeur approché de la taille (10, 100, 1000, etc.)
			ContentData contentData = (ContentData) nodeService.getProperty(nodeRef, ContentModel.PROP_CONTENT);
			if (contentData != null) {
				long size = contentData.getSize();
				long sizeLog = (long) Math.pow(10, (int) Math.log10(size));
				nodeService.setProperty(nodeRef, propertyText, Long.toString(sizeLog));
			} else {
				nodeService.setProperty(nodeRef, propertyText, null);
			}
		});
	}
	
}
