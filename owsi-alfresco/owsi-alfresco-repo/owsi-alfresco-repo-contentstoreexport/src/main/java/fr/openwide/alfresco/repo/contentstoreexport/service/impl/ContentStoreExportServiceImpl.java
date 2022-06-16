package fr.openwide.alfresco.repo.contentstoreexport.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.management.MBeanServerConnection;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.management.JmxDumpUtil;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.transaction.RetryingTransactionHelper;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.AccessPermission;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.version.Version;
import org.alfresco.service.cmr.version.VersionHistory;
import org.alfresco.service.cmr.version.VersionService;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ISO9075;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.TeeOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.repo.contentstoreexport.model.ContentStoreExportParams;
import fr.openwide.alfresco.repo.contentstoreexport.model.ContentStoreExportParams.PathType;
import fr.openwide.alfresco.repo.contentstoreexport.service.ContentStoreExportService;

/**
 * Module d'export focalisé par défaut sur les dépôts locaux nécessaire au lancement. 
 * Il est cependant possible d'enrichir le comportement par défaut de ce module via des paramètres d'url.
 * 
 * Pour accéder au service, utilisez l'url: "http://localhost:8080/alfresco/s/owsi/contentstoreexport.zip"
 * Par défaut le module exporte :
 * 
 * - la valeur retournée par le path "app:company_home/app:dictionary", 
 * - la valeur retournée par la requête "cm:name:surf-config"
 * - finalement la valeur du nodeRef "system://system".
 * 
 * Il est possible d'enrichir le comportement du module en complétant l'url avec des paramètres. 
 * Chaque paramètres peut contenir plusieurs valeurs séparées par une virgule:
 * 
 * - paths: permet d'enrichir le module d'export avec un ou plusieurs path Alfresco. (i.e: .../s/owsi/contentstoreexport.zip?path=app:company_home,app:dictionnary)
 * - queries: permet d'enrichir le module d'export avec une ou plusieurs requêtes Alfresco.
 * - nodeRefs: permet d'enrichir le module d'export avec un ou plusieurs NodeRefs? Alfresco.
 * 
 * Il est possible d'enrichir plusieurs paramètres en une seule fois :
 * (i.e .../s/owsi/contentstoreexport.zip?path=app:company_home,app:dictionnary&queries==cm:name:surf-config)
 */
public class ContentStoreExportServiceImpl implements ContentStoreExportService {

	private static final String OWSI_CONTENTSTOREEXPORT_VIEW_MODULE_NAME = "owsi-contentstoreexport-view";
	private static final String SPRING_BEAN_NAMESPACE = "http://www.springframework.org/schema/beans";

	private DateFormat iso8601 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	private static final String STORE_PREFIX = "store:/";
	private static final String SAMPLE_EXTENSION = ".sample";
	private static final String TOMCAT_HOME = System.getProperty("catalina.base");

	private static final Set<String> PROPERTIES_NOT_TO_EXPORT = new HashSet<String>(Arrays.asList(new String[] {
		"sys:store-identifier",
		"sys:store-protocol",
		"sys:node-dbid",
		"cm:lastThumbnailModification"
	}));
	
	private final Logger LOGGER = LoggerFactory.getLogger(ContentStoreExportServiceImpl.class);

	public static StringBuilder configurationLogger = new StringBuilder();
	
	private DescriptorService descriptorService;
	private NodeService nodeService;
	private SearchService searchService;
	private ContentService contentService;
	private PermissionService permissionService;
	private SiteService siteService;
	private VersionService versionService;
	private RetryingTransactionHelper retryingTransactionHelper;
	private NamespacePrefixResolver namespacePrefixResolver;
	private MBeanServerConnection mbeanServer;

	private FileFolderService fileFolderService;
	private Repository repositoryHelper;

	private String contentstoreexportPaths;
	private String contentstoreexportQueries;
	private String contentstoreexportVersion;

	@Override
	public void export(OutputStream outputStream, String fileName, ContentStoreExportParams params) throws IOException {
		long startTime = System.currentTimeMillis();
		Set<String> processedNodes = new HashSet<String>();
		LOGGER.info("lancement de l'export.");
		Properties properties = new Properties();
		properties.setProperty("contentstoreexport.version", contentstoreexportVersion);
		properties.setProperty("alfresco.version", descriptorService.getCurrentRepositoryDescriptor().getVersion());
		properties.setProperty("export.date", new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss").format(new Date()));
		Set<NodeRef> rootNodesToExport = getRootNodesToExport(params, properties);
		// debut du stockage des fichiers dans le zip
		
		AtomicInteger nbFiles = new AtomicInteger(0);
		AtomicLong totalVolume = new AtomicLong(0L);
		
		outputStream = getOutputStream(outputStream, fileName, params);
		ZipOutputStream zipOutPutStream = null;
		try {
			zipOutPutStream = new ZipOutputStream(outputStream);
			for (NodeRef root : rootNodesToExport) {
				LOGGER.info("Export node : " + root);
				recurseThroughNodeRefChilds(root, zipOutPutStream, processedNodes, nbFiles, totalVolume, params, 1);
			}

			LOGGER.info("Nombre de fichiers exportés: " + nbFiles);
			properties.setProperty("fichiers.exportes.nb", "" + nbFiles);
			properties.setProperty("fichiers.exportes.size", "" + totalVolume);
			LOGGER.info("Fin de l'export.");

			if (params.acp) {
				createView(params, rootNodesToExport, zipOutPutStream);
			} else {
				// Export tomcat/shared/classes/
				exportLocalFiles(zipOutPutStream, new File(TOMCAT_HOME + "/shared/classes"));
	
				// Ajout du JMX dump, normalement accessible depuis /alfresco/service/api/admin/jmxdump
				zipOutPutStream.putNextEntry(new ZipEntry("jars.txt"));
				try {
					PrintWriter writer = new PrintWriter(zipOutPutStream);
					exportListJars(writer, new File(TOMCAT_HOME));
					writer.flush();
				} finally {
					zipOutPutStream.closeEntry();
				}
				
				// Ajout du JMX dump, normalement accessible depuis /alfresco/service/api/admin/jmxdump
				zipOutPutStream.putNextEntry(new ZipEntry("jmxdump.txt"));
				try {
					PrintWriter writer = new PrintWriter(zipOutPutStream);
					try {
						JmxDumpUtil.dumpConnection(mbeanServer, writer);
					} catch (Throwable t) {
						// Si dans une vielle version cela ne passe pas, on ignore
						LOGGER.error("Ignore ", t);
						t.printStackTrace(writer);
					}
					writer.flush();
				} finally {
					zipOutPutStream.closeEntry();
				}
	
				// Ajout du ConfigurationLogger, s'il est disponible
				if (configurationLogger.length() > 0) {
					zipOutPutStream.putNextEntry(new ZipEntry("configurationlogger.txt"));
					OutputStreamWriter out = new OutputStreamWriter(zipOutPutStream, StandardCharsets.UTF_8);
					out.append(configurationLogger.toString());
					out.flush();
					zipOutPutStream.closeEntry();
				}
			}
			
			// fin des traitements (aucun traitement supplémentaire ne doit etre fait au dela de ce point)
			/* calcul du temps d'execution, intégration de la donnée dans le .properties et ajout du .properties
			dans le zip */
			long stopTime = System.currentTimeMillis();
			String elapsedTime = secondToTimeConverter(stopTime - startTime);
			properties.setProperty("temps.execution", elapsedTime);
			zipOutPutStream.putNextEntry(new ZipEntry("ContentStoreExport.properties"));
			try {
				properties.store(zipOutPutStream, "Généré automatiquement par le service ContentStoreExport @SMILE");
			} finally {
				zipOutPutStream.closeEntry();
			}
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		} finally {
			IOUtils.closeQuietly(zipOutPutStream);
		}
	}

	/** @see org.alfresco.repo.importer.view.ViewParser */
	private void createView(ContentStoreExportParams params, Set<NodeRef> rootNodesToExport,
			ZipOutputStream zipOutPutStream)
			throws IOException, XMLStreamException, FactoryConfigurationError, UnsupportedEncodingException {
		
		// View files
		String exportKey = new SimpleDateFormat("yyyy.MM.dd-hh.mm.ss").format(new Date());
		
		for (NodeRef nodeRef : rootNodesToExport) {
			String dataFile = "alfresco/module/"+ OWSI_CONTENTSTOREEXPORT_VIEW_MODULE_NAME + "/view-" + exportKey + "-" + nodeRef.getId() + "-data.xml";
			zipOutPutStream.putNextEntry(new ZipEntry(dataFile));
			try {
				XMLStreamWriter xmlView = XMLOutputFactory.newFactory()
						.createXMLStreamWriter(zipOutPutStream, "UTF-8");
				xmlView.writeStartDocument("UTF-8", "1.0");
				xmlView.writeStartElement("view", "view", NamespaceService.REPOSITORY_VIEW_1_0_URI);
		
				ChildAssociationRef primaryParent = nodeService.getPrimaryParent(nodeRef);
				createViewForNode(primaryParent, xmlView, params);
		
				xmlView.writeEndElement();
				xmlView.writeEndDocument();
				xmlView.flush();
			} finally {
				zipOutPutStream.closeEntry();
			}
		}

		// module.properties
		zipOutPutStream.putNextEntry(new ZipEntry("alfresco/module/"+ OWSI_CONTENTSTOREEXPORT_VIEW_MODULE_NAME + "/module.properties"));
		try {
			zipOutPutStream.write(("module.id=" + OWSI_CONTENTSTOREEXPORT_VIEW_MODULE_NAME).getBytes());
			zipOutPutStream.write("module.version=1.0".getBytes());
		} finally {
			zipOutPutStream.closeEntry();
		}
		
		// Spring context
		zipOutPutStream.putNextEntry(new ZipEntry("alfresco/module/"+ OWSI_CONTENTSTOREEXPORT_VIEW_MODULE_NAME + "/view-" + exportKey + "-context.xml"));
		try {
			XMLStreamWriter xmlSpring = XMLOutputFactory.newFactory()
					.createXMLStreamWriter(zipOutPutStream, "UTF-8");
			xmlSpring.setDefaultNamespace(SPRING_BEAN_NAMESPACE);
			xmlSpring.writeStartDocument("UTF-8", "1.0");
			xmlSpring.writeStartElement(SPRING_BEAN_NAMESPACE, "beans");
			
			xmlSpring.writeStartElement(SPRING_BEAN_NAMESPACE, "bean");
			xmlSpring.writeAttribute("id", "owsi.contentstoreexport.bootstrap.view." + exportKey);
			xmlSpring.writeAttribute("class", "org.alfresco.repo.module.ImporterModuleComponent");
			xmlSpring.writeAttribute("parent", "module.baseComponent");
			
			writeProperty(xmlSpring,"moduleId", "owsi.contentstoreexport");
			writeProperty(xmlSpring,"name", "owsi.contentstoreexport.bootstrap.view." + exportKey);
			writeProperty(xmlSpring,"sinceVersion", "1.0");
			writeProperty(xmlSpring,"appliesFromVersion", "1.0");
			writeProperty(xmlSpring,"importer", "spacesBootstrap");
			
			xmlSpring.writeStartElement("property");
			xmlSpring.writeAttribute("name", "bootstrapViews");
			xmlSpring.writeStartElement("list");
			
			for (NodeRef nodeRef : rootNodesToExport) {
				xmlSpring.writeStartElement("props");
				
				String path = nodeService.getPath(nodeRef).toPrefixString(namespacePrefixResolver);
				xmlSpring.writeStartElement("prop");
				xmlSpring.writeAttribute("key", "path");
				xmlSpring.writeCharacters(path);
				xmlSpring.writeEndElement();
	
				String dataFile = "alfresco/module/"+ OWSI_CONTENTSTOREEXPORT_VIEW_MODULE_NAME + "/view-" + exportKey + "-" + nodeRef.getId() + "-data.xml";
				xmlSpring.writeStartElement("prop");
				xmlSpring.writeAttribute("key", "location");
				xmlSpring.writeCharacters(dataFile);
				xmlSpring.writeEndElement();
	
				xmlSpring.writeEndElement(); // props
			}
			
			xmlSpring.writeEndElement(); // list
			xmlSpring.writeEndElement(); // property
	
			xmlSpring.writeEndElement(); // bean
			xmlSpring.writeEndElement(); // beans
			xmlSpring.writeEndDocument();
			xmlSpring.flush();
		} finally {
			zipOutPutStream.closeEntry();
		}
	}

	private void writeProperty(XMLStreamWriter xmlSpring, String name, String value) throws XMLStreamException {
		xmlSpring.writeStartElement("property");
		xmlSpring.writeAttribute("name", name);
		xmlSpring.writeAttribute("value", value);
		xmlSpring.writeEndElement();
	}
	
	private void writeStartElement(XMLStreamWriter writer, QName qname) throws XMLStreamException {
		writer.writeStartElement(QName.splitPrefixedQName(qname.toPrefixString(namespacePrefixResolver))[0], 
				qname.getLocalName(), 
				qname.getNamespaceURI());
	}

	private void createViewForNode(ChildAssociationRef parent, XMLStreamWriter writer, ContentStoreExportParams params) throws XMLStreamException {
		NodeRef nodeRef = parent.getChildRef();
		writeStartElement(writer, nodeService.getType(nodeRef));
		
		writer.writeAttribute("view", NamespaceService.REPOSITORY_VIEW_1_0_URI, "childName", parent.getQName().toPrefixString(namespacePrefixResolver));
		
		Set<QName> aspects = nodeService.getAspects(nodeRef);
		if (! aspects.isEmpty()) {
			writer.writeStartElement("view", "aspects", NamespaceService.REPOSITORY_VIEW_1_0_URI);
			for (QName aspect : aspects) {
				writeStartElement(writer, aspect);
				writer.writeEndElement();
			}
			writer.writeEndElement();
		}
		
		writer.writeStartElement("view", "properties", NamespaceService.REPOSITORY_VIEW_1_0_URI);
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		for (Entry<QName, Serializable> pv : properties.entrySet()) {
			QName property = pv.getKey();
			if (! PROPERTIES_NOT_TO_EXPORT.contains(property.toPrefixString(namespacePrefixResolver))) {
				writeStartElement(writer, property);
				
				if (pv.getValue() == null) {
					// nop
				} else if (pv.getValue() instanceof Date) {
					writer.writeCharacters(iso8601.format((Date) pv.getValue()));
				} else if (pv.getValue() instanceof ContentData) {
					ContentData originalContentData = (ContentData) pv.getValue();
					ContentData contentData = new ContentData(
							"classpath:" + getContentPath(nodeRef, property, originalContentData, params),
							originalContentData.getMimetype(), 
							originalContentData.getSize(), 
							originalContentData.getEncoding(), 
							originalContentData.getLocale());
					writer.writeCharacters(contentData.getInfoUrl());
				} else {
					writer.writeCharacters(pv.getValue().toString());
				}
				writer.writeEndElement();
			}
		}
		writer.writeEndElement();
		
		if (params.acpPermissions) {
			Set<AccessPermission> acl = permissionService.getAllSetPermissions(nodeRef);
			List<AccessPermission> aclFiltered = new ArrayList<AccessPermission>();
			for (AccessPermission ace : acl) {
				if (ace.isSetDirectly()) {
					aclFiltered.add(ace);
				}
			}
			if (! aclFiltered.isEmpty()) {
				writer.writeStartElement("view", "acl", NamespaceService.REPOSITORY_VIEW_1_0_URI);
				for (AccessPermission ace : aclFiltered) {
					writer.writeStartElement("view", "ace", NamespaceService.REPOSITORY_VIEW_1_0_URI);
					writer.writeAttribute("view", NamespaceService.REPOSITORY_VIEW_1_0_URI, "access", ace.getAccessStatus().name());
					
					writer.writeStartElement("view", "authority", NamespaceService.REPOSITORY_VIEW_1_0_URI);
					writer.writeCharacters(ace.getAuthority());
					writer.writeEndElement();
					
					writer.writeStartElement("view", "permission", NamespaceService.REPOSITORY_VIEW_1_0_URI);
					writer.writeCharacters(ace.getPermission());
					writer.writeEndElement();
					
					writer.writeEndElement();
				}
				writer.writeEndElement();
			}
		}
		
		// Children
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		Map<QName, List<ChildAssociationRef>> mapChildren = new LinkedHashMap<QName, List<ChildAssociationRef>>();
		for (ChildAssociationRef child : children) {
			List<ChildAssociationRef> list = mapChildren.get(child.getTypeQName());
			if (list == null) {
				mapChildren.put(child.getTypeQName(), list = new ArrayList<ChildAssociationRef>());
			}
			list.add(child);
		}
		for (Entry<QName, List<ChildAssociationRef>> entry : mapChildren.entrySet()) {
			writeStartElement(writer, entry.getKey());
			for (ChildAssociationRef child : entry.getValue()) {
				createViewForNode(child, writer, params);
			}
			writer.writeEndElement();
		}		
		writer.writeEndElement();
	}

	// parcours recursif du dossier shared/classes de Tomcat
	private void exportLocalFiles(ZipOutputStream zipOutPutStream, File root) throws IOException {
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			if (files != null) {
				for (File file : files) {
					String path = file.getAbsolutePath();
					// substring du path pour n'avoir que shared/classes dans le zip
					if (path.startsWith(TOMCAT_HOME)) {
						path = path.substring(TOMCAT_HOME.length()); 
					}
					path = path.replace('\\', '/');
					if (path.startsWith("/")) {
						path = path.substring("/".length());
					}
					// on ignore les fichiers *.sample
					if (!file.isDirectory() && !path.endsWith(SAMPLE_EXTENSION)) {
						zipOutPutStream.putNextEntry(new ZipEntry(path));
						FileInputStream fileIn = new FileInputStream(file);
						try {
							IOUtils.copy(fileIn, zipOutPutStream);
							zipOutPutStream.closeEntry();
						} finally {
							IOUtils.closeQuietly(fileIn);
						}
					}
					exportLocalFiles(zipOutPutStream, file);
				}
			}
		}
	}
	
	private void exportListJars(PrintWriter writer, File root) throws IOException {
		if (root.isDirectory()) {
			File[] files = root.listFiles();
			if (files != null) {
				for (File file : files) {
					String path = file.getAbsolutePath();
					if (path.startsWith(TOMCAT_HOME)) {
						path = path.substring(TOMCAT_HOME.length()); 
					}
					if (!file.isDirectory() && path.endsWith(".jar")) {
						writer.println(path);
					}
					exportListJars(writer, file);
				}
			}
		}
	}
	
	private Set<NodeRef> getRootNodesToExport(ContentStoreExportParams params, Properties properties) {
		Set<NodeRef> rootNodesToExport = new LinkedHashSet<NodeRef>();
		
		// Paths
		if (params.paths != null) {
			properties.setProperty("paths.parametres.req", Arrays.asList(params.paths).toString());
			for (String path : params.paths) {
				if (!path.trim().isEmpty()) {
					rootNodesToExport.add(getByPath(path));
				}
			}
		}

		// Queries
		if (params.queries != null) {
			properties.setProperty("queries.parametres.req", Arrays.asList(params.queries).toString());
			for (String query : params.queries) {
				if (!query.trim().isEmpty()) {
					ResultSet resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
							SearchService.LANGUAGE_FTS_ALFRESCO, query);
					rootNodesToExport.addAll(resultSet.getNodeRefs());
					resultSet.close();
				}
			}
		}
		// NodeRefs
		if (params.nodeRefs != null) {
			properties.setProperty("noderefs.parametres.req", Arrays.asList(params.nodeRefs).toString());
			for (String nodeRef : params.nodeRefs) {
				if (!nodeRef.trim().isEmpty()) {
					rootNodesToExport.add(new NodeRef(nodeRef));
				}
			}
		}
		
		// Sites
		if (params.sites != null) {
			properties.setProperty("sites.parametres.req", Arrays.asList(params.sites).toString());
			for (String site : params.sites) {
				if (!site.trim().isEmpty()) {
					rootNodesToExport.add(siteService.getSite(site).getNodeRef());
				}
			}
		}
		
		// Base
		if (params.exportBase && ! params.acp) {
			//la racine system://syteme est ajoutée en dur via un noderef
			rootNodesToExport.add(nodeService.getRootNode(new StoreRef("system://system")));

			properties.setProperty("paths.parametres.config", contentstoreexportPaths);
			for (String path : contentstoreexportPaths.split(",")) {
				if (!path.trim().isEmpty()) {
					rootNodesToExport.add(getByPath(path));
				}
			}
			properties.setProperty("queries.parametres.config", contentstoreexportQueries);
			for (String query : contentstoreexportQueries.split(",")) {
				if (!query.trim().isEmpty()) {
					ResultSet resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
							SearchService.LANGUAGE_FTS_ALFRESCO, query);
					rootNodesToExport.addAll(resultSet.getNodeRefs());
					resultSet.close();
				}
			}
		}
		
		// All
		if (params.exportAll) {
			properties.setProperty("all.parametres.req", "true");
			rootNodesToExport.addAll(nodeService.getAllRootNodes(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE));
		}
		
		LOGGER.info("Nombre de racines trouvées: " + rootNodesToExport.size());
		return rootNodesToExport;
	}

	private NodeRef getByPath(String path) {
		NodeRef nodeRef = nodeService.getRootNode(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE);
		
		for (String childName : path.split("/")) {
			if (!childName.trim().isEmpty()) {
				childName = ISO9075.decode(childName);
				List<ChildAssociationRef> assocs = nodeService.getChildAssocs(nodeRef, RegexQNamePattern.MATCH_ALL,
						QName.createQName(childName, namespacePrefixResolver), true);
				if (assocs.size() != 1) {
					throw new IllegalArgumentException(path + " : " + assocs.size());
				}
				nodeRef = assocs.get(0).getChildRef();
			}
		}
		return nodeRef;
	}

	private void recurseThroughNodeRefChilds(NodeRef nodeRef, final ZipOutputStream zipOutPutStream, final Set<String> processedNodes,
			final AtomicInteger nbFiles, final AtomicLong totalVolume, final ContentStoreExportParams params, final int depth)
			throws IOException, XMLStreamException {
		
		if (params.since != null) {
			Date modified = (Date) nodeService.getProperty(nodeRef, ContentModel.PROP_MODIFIED);
			Duration duration = Duration.parse(params.since);
			Date limit = Date.from(Instant.from(duration.subtractFrom(Instant.now())));
			if (limit.after(modified)) {
				return;
			}
		}
		
		LOGGER.debug("Export node : " + nodeRef);
		
		//recupération des properties du node courant
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		analyzeProperties(nodeRef, zipOutPutStream, processedNodes, nbFiles, totalVolume, params, properties);
		
		if (       params.exportVersions 
				&& params.getPathType() == PathType.CONTENTSTORE 
				&& versionService.isVersioned(nodeRef)) {
			VersionHistory versionHistory = versionService.getVersionHistory(nodeRef);
			for (Version version : versionHistory.getAllVersions()) {
				Map<String, Serializable> versionProperties = version.getVersionProperties();
				Map<QName, Serializable> versionProperties2 = new LinkedHashMap<QName, Serializable>();
				for (Entry<String, Serializable> entry : versionProperties.entrySet()) {
					// On n'a que le local name. Le content devrait être un cm:content.
					QName key = QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, entry.getKey());
					versionProperties2.put(key, entry.getValue());
				}
				analyzeProperties(nodeRef, zipOutPutStream, processedNodes, nbFiles, totalVolume, params, versionProperties2);
			}
		}
		
		if (params.getPathType() == PathType.BULK) {
			String propertiesFile = getContentPath(nodeRef, ContentModel.PROP_CONTENT, null, params)
					+ ".metadata.properties.xml";
			if (processedNodes.add(propertiesFile)) {
				zipOutPutStream.putNextEntry(new ZipEntry(propertiesFile));
				
				try {
					XMLStreamWriter xmlWriter = XMLOutputFactory.newFactory()
							.createXMLStreamWriter(zipOutPutStream, "UTF-8");
					xmlWriter.writeStartDocument("UTF-8", "1.0");
					xmlWriter.writeStartElement("properties");

					xmlWriter.writeStartElement("entry");
					xmlWriter.writeAttribute("key", "type");
					xmlWriter.writeCharacters(nodeService.getType(nodeRef).toPrefixString(namespacePrefixResolver));
					xmlWriter.writeEndElement();

					xmlWriter.writeStartElement("entry");
					xmlWriter.writeAttribute("key", "aspects");
					
					boolean firstAspect = true;
					for (QName aspect : nodeService.getAspects(nodeRef)) {
						if (firstAspect) {
							firstAspect = false;
						} else {
							xmlWriter.writeCharacters(",");
						}
						xmlWriter.writeCharacters(aspect.toPrefixString(namespacePrefixResolver));
					}
					xmlWriter.writeEndElement();
					
					for (Entry<QName, Serializable> property : properties.entrySet()) {
						String propertyName = property.getKey().toPrefixString(namespacePrefixResolver);
						if (! (property.getValue() instanceof ContentData) 
							&& ! PROPERTIES_NOT_TO_EXPORT.contains(propertyName)) {
							xmlWriter.writeStartElement("entry");
							xmlWriter.writeAttribute("key", propertyName);
							
							if (property.getValue() == null) {
								// nop
							} else if (property.getValue() instanceof Date) {
								xmlWriter.writeCharacters(iso8601.format((Date) property.getValue()));
							} else {
								xmlWriter.writeCharacters(property.getValue().toString());
							}
							xmlWriter.writeEndElement();
						}
					}
					
					xmlWriter.writeEndElement();
					xmlWriter.writeEndDocument();
					xmlWriter.flush();
				} finally {
					zipOutPutStream.closeEntry();
				}
			}
		}
		
		//recursion dans les fils
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		for (ChildAssociationRef childAssoc : children) {
			if (   params.getPathType() == PathType.CONTENTSTORE
				|| ContentModel.ASSOC_CONTAINS.equals(childAssoc.getTypeQName())) {
				final NodeRef childNodeRef = childAssoc.getChildRef();
				
				if (depth % params.newTransactionEveryDepth == 1) {
					retryingTransactionHelper.doInTransaction(new RetryingTransactionCallback<Void>() {
						@Override
						public Void execute() throws Throwable {
							recurseThroughNodeRefChilds(childNodeRef, zipOutPutStream, processedNodes, nbFiles, totalVolume, params, depth+1);
							return null;
						}
					}, true, true);
				} else {
					recurseThroughNodeRefChilds(childNodeRef, zipOutPutStream, processedNodes, nbFiles, totalVolume, params, depth+1);
				}
			}
		}
	}

	private void analyzeProperties(NodeRef nodeRef, ZipOutputStream zipOutPutStream, Set<String> processedNodes,
			AtomicInteger nbFiles, AtomicLong totalVolume, ContentStoreExportParams params,
			Map<QName, Serializable> properties) throws IOException {
		for (Entry<QName, Serializable> property : properties.entrySet()) {
			//seul un content data nous interesse
			if (property.getValue() instanceof ContentData) {
				ContentData contentData = (ContentData) property.getValue();
				String contentUrl = getContentPath(nodeRef, property.getKey(), contentData, params);
				
				if (processedNodes.add(contentUrl)) {
					nbFiles.incrementAndGet();
					totalVolume.addAndGet(contentData.getSize());
					
					if (params.exportContent) {
						//ajout de l'entrée au zip
						ContentReader reader = contentService.getReader(nodeRef, property.getKey());
						if (reader != null) {
							InputStream inputStream = reader.getContentInputStream();
							zipOutPutStream.putNextEntry(new ZipEntry(contentUrl));
							try {
								IOUtils.copy(inputStream, zipOutPutStream);
							} finally {
								IOUtils.closeQuietly(inputStream);
								zipOutPutStream.closeEntry();
							}
						} else {
							LOGGER.warn(nodeRef + " / " + property.getKey() + " has a null reader.");
						}
					}
				}
			}
		}
	}

	private String getContentPath(NodeRef nodeRef, QName property, ContentData contentData, ContentStoreExportParams params) {
		switch (params.getPathType()) {
		case CONTENTSTORE: 
			String contentUrl = contentData.getContentUrl(); 
			//on remplace "/store://" par "/contentstore"
			if (contentUrl.startsWith(STORE_PREFIX)) {
				contentUrl = contentUrl.substring(STORE_PREFIX.length());
			}
			contentUrl = "contentstore" + contentUrl;
			return contentUrl;
		case ALFRESCO: 
		case BULK:
			return contentUrl = "export" 
				+ nodeService.getPath(nodeRef).toDisplayPath(nodeService, permissionService)
				+ ((property.equals(ContentModel.PROP_CONTENT)) ? "" : "/" + property.getLocalName())
				+ "/" + nodeService.getProperty(nodeRef, ContentModel.PROP_NAME);
		default: 
			throw new IllegalStateException(params.getPathType().name());
		}
	}
	
	private String secondToTimeConverter (long milliseconds) {
		long seconds = milliseconds / 1000;
		long s = seconds % 60;
		long m = (seconds / 60) % 60;
		long h = (seconds / (60 * 60)) % 24;
		return String.format("%dh %02dmn %02ds", h,m,s);
	}
	
	private OutputStream getOutputStream(OutputStream outputStream, 
			String fileName, 
			ContentStoreExportParams params) throws IOException {
		if (params.writeToDisk != null) {
			File file = new File(params.writeToDisk, fileName);
			outputStream = new TeeOutputStream(outputStream, new FileOutputStream(file));
		}
		if (params.writeToAlfresco != null) {
			NodeRef parentNodeRef = (params.writeToAlfresco.trim().length() > 0)
					? getByPath(params.writeToAlfresco)
					: repositoryHelper.getUserHome(repositoryHelper.getFullyAuthenticatedPerson());
			FileInfo fileInfo = fileFolderService.create(parentNodeRef, fileName, ContentModel.TYPE_CONTENT);
			ContentWriter writer = contentService.getWriter(fileInfo.getNodeRef(), ContentModel.PROP_CONTENT, true);
			outputStream = new TeeOutputStream(outputStream, writer.getContentOutputStream());
		}
		return outputStream;
	}
 
	
	public void setDescriptorService(DescriptorService descriptorService) {
		this.descriptorService = descriptorService;
	}
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.nodeService = serviceRegistry.getNodeService();
		this.searchService = serviceRegistry.getSearchService();
		this.contentService = serviceRegistry.getContentService();
		this.permissionService = serviceRegistry.getPermissionService();
		this.namespacePrefixResolver = serviceRegistry.getNamespaceService();
		this.siteService = serviceRegistry.getSiteService();
		this.versionService = serviceRegistry.getVersionService();
		this.retryingTransactionHelper = serviceRegistry.getRetryingTransactionHelper();
	}
	
	public void setContentstoreexportVersion(String contentstoreexportVersion) {
		this.contentstoreexportVersion = contentstoreexportVersion;
	}

	public void setContentstoreexportPaths(String contentstoreexportPaths) {
		this.contentstoreexportPaths = contentstoreexportPaths;
	}

	public void setContentstoreexportQueries(String contentstoreexportQueries) {
		this.contentstoreexportQueries = contentstoreexportQueries;
	}
	public void setMBeanServer(MBeanServerConnection mbeanServer) {
		this.mbeanServer = mbeanServer;
	}
	public void setRepositoryHelper(Repository repositoryHelper) {
		this.repositoryHelper = repositoryHelper;
	}
	public void setFileFolderService(FileFolderService fileFolderService) {
		this.fileFolderService = fileFolderService;
	}
}
