package fr.openwide.alfresco.repo.contentstoreexport.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
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

import org.alfresco.model.ContentModel;
import org.alfresco.repo.management.JmxDumpUtil;
import org.alfresco.service.ServiceRegistry;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentData;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.ResultSet;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.descriptor.DescriptorService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.namespace.RegexQNamePattern;
import org.alfresco.util.ISO9075;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.repo.contentstoreexport.model.ContentStoreExportParams;
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

	private static final String STORE_PREFIX = "store:/";
	private static final String SAMPLE_EXTENSION = ".sample";
	private static final String TOMCAT_HOME = System.getProperty("catalina.base");

	private final Logger LOGGER = LoggerFactory.getLogger(ContentStoreExportServiceImpl.class);

	public static StringBuilder configurationLogger = new StringBuilder();
	
	private DescriptorService descriptorService;
	private NodeService nodeService;
	private SearchService searchService;
	private ContentService contentService;
	private PermissionService permissionService;
	private NamespacePrefixResolver namespacePrefixResolver;
	private MBeanServerConnection mbeanServer;

	private String contentstoreexportPaths;
	private String contentstoreexportQueries;
	private String contentstoreexportVersion;

	@Override
	public void export(OutputStream outputStream, ContentStoreExportParams params) throws IOException {
		long startTime = System.currentTimeMillis();
		Set<String> processedNodes = new HashSet<String>();
		LOGGER.info("lancement de l'export.");
		Properties properties = new Properties();
		properties.setProperty("contentstoreexport.version", contentstoreexportVersion);
		properties.setProperty("alfresco.version", descriptorService.getCurrentRepositoryDescriptor().getVersion());
		Set<NodeRef> rootNodesToExport = getRootNodesToExport(params, properties);
		// debut du stockage des fichiers dans le zip
		
		AtomicInteger nbFiles = new AtomicInteger(0);
		AtomicLong totalVolume = new AtomicLong(0L);
		
		ZipOutputStream zipOutPutStream = null;
		try {
			zipOutPutStream = new ZipOutputStream(outputStream);
			for (NodeRef root : rootNodesToExport) {
				LOGGER.info("Export node : " + root);
				recurseThroughNodeRefChilds(root, zipOutPutStream, processedNodes, nbFiles, totalVolume, params);
			}
			LOGGER.info("Nombre de fichiers exportés: " + nbFiles);
			properties.setProperty("fichiers.exportes.nb", "" + nbFiles);
			properties.setProperty("fichiers.exportes.size", "" + totalVolume);
			LOGGER.info("Fin de l'export.");

			exportLocalFiles(zipOutPutStream, new File(TOMCAT_HOME + "/shared/classes"));

			// Ajout du JMX dump, normalement accessible depuis /alfresco/service/api/admin/jmxdump
			zipOutPutStream.putNextEntry(new ZipEntry("jars.txt"));
			PrintWriter writer = new PrintWriter(zipOutPutStream);
			exportListJars(writer, new File(TOMCAT_HOME));
			writer.flush();
			zipOutPutStream.closeEntry();
			
			// Ajout du JMX dump, normalement accessible depuis /alfresco/service/api/admin/jmxdump
			zipOutPutStream.putNextEntry(new ZipEntry("jmxdump.txt"));
			writer = new PrintWriter(zipOutPutStream);
			try {
				JmxDumpUtil.dumpConnection(mbeanServer, writer);
			} catch (Throwable t) {
				// Si dans une vielle version cela ne passe pas, on ignore
				LOGGER.error("Ignore ", t);
				t.printStackTrace(writer);
			}
			writer.flush();
			zipOutPutStream.closeEntry();

			// Ajout du ConfigurationLogger, s'il est disponible
			if (configurationLogger.length() > 0) {
				zipOutPutStream.putNextEntry(new ZipEntry("configurationlogger.txt"));
				OutputStreamWriter out = new OutputStreamWriter(zipOutPutStream, StandardCharsets.UTF_8);
				out.append(configurationLogger.toString());
				out.flush();
				zipOutPutStream.closeEntry();
			}
			
			// fin des traitements (aucun traitement supplémentaire ne doit etre fait au dela de ce point)
			/* calcul du temps d'execution, intégration de la donnée dans le .properties et ajout du .properties
			dans le zip */
			long stopTime = System.currentTimeMillis();
			String elapsedTime = secondToTimeConverter(stopTime - startTime);
			properties.setProperty("temps.execution", elapsedTime);
			zipOutPutStream.putNextEntry(new ZipEntry("ContentStoreExport.properties"));
			properties.store(zipOutPutStream, "Généré automatiquement par le service ContentStoreExport @SMILE");
			zipOutPutStream.closeEntry();
		} finally {
			IOUtils.closeQuietly(zipOutPutStream);
		}
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
		Set<NodeRef> rootNodesToExport = new HashSet<NodeRef>();
		
		// All
		if (params.isExportAll()) {
			rootNodesToExport.addAll(nodeService.getAllRootNodes(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE));
		}
		
		// Paths
		if (params.paths != null) {
			properties.setProperty("paths.parametres.req", params.paths);
			for (String path : params.paths.split(",")) {
				if (!path.trim().isEmpty()) {
					rootNodesToExport.add(getByPath(path));
				}
			}
		}
		properties.setProperty("paths.parametres.config", contentstoreexportPaths);
		for (String path : contentstoreexportPaths.split(",")) {
			if (!path.trim().isEmpty()) {
				rootNodesToExport.add(getByPath(path));
			}
		}
		// Queries
		if (params.queries != null) {
			properties.setProperty("queries.parametres.req", params.queries);
			for (String query : params.queries.split(",")) {
				if (!query.trim().isEmpty()) {
					ResultSet resultSet = searchService.query(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE,
							SearchService.LANGUAGE_FTS_ALFRESCO, query);
					rootNodesToExport.addAll(resultSet.getNodeRefs());
					resultSet.close();
				}
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
		// NodeRefs
		if (params.nodeRefs != null) {
			properties.setProperty("noderefs.parametres.req", params.nodeRefs);
			for (String nodeRef : params.nodeRefs.split(",")) {
				if (!nodeRef.trim().isEmpty()) {
					rootNodesToExport.add(new NodeRef(nodeRef));
				}
			}
		}
		//la racine system://syteme est ajoutée en dur via un noderef
		rootNodesToExport.add(nodeService.getRootNode(new StoreRef("system://system")));
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

	private void recurseThroughNodeRefChilds(NodeRef nodeRef, ZipOutputStream zipOutPutStream, Set<String> processedNodes,
			AtomicInteger nbFiles, AtomicLong totalVolume, ContentStoreExportParams params)
			throws IOException {
		LOGGER.debug("Export node : " + nodeRef);
		
		//recupération des properties du node courant
		Map<QName, Serializable> properties = nodeService.getProperties(nodeRef);
		for (Entry<QName, Serializable> property : properties.entrySet()) {
			//seul un content data nous interesse
			if (property.getValue() instanceof ContentData) {
				ContentData contentData = (ContentData) property.getValue();
				nbFiles.incrementAndGet();
				totalVolume.addAndGet(contentData.getSize());
				
				if (params.isExportContent()) {
					ContentReader reader = contentService.getReader(nodeRef, property.getKey());
					InputStream inputStream = reader.getContentInputStream();
					try {
						String contentUrl = getContentUrl(nodeRef, property.getKey(), contentData, params);
						if (processedNodes.add(contentUrl)) {
							//ajout de l'entrée au zip
							zipOutPutStream.putNextEntry(new ZipEntry(contentUrl));
							IOUtils.copy(inputStream, zipOutPutStream);
						}
					} finally {
						IOUtils.closeQuietly(inputStream);
						zipOutPutStream.closeEntry();
					}
				}
			}
		}
		//recursion dans les fils
		List<ChildAssociationRef> children = nodeService.getChildAssocs(nodeRef);
		for (ChildAssociationRef childAssoc : children) {
			NodeRef childNodeRef = childAssoc.getChildRef();
			recurseThroughNodeRefChilds(childNodeRef, zipOutPutStream, processedNodes, nbFiles, totalVolume, params);
		}
	}

	private String getContentUrl(NodeRef nodeRef, QName property, ContentData contentData, ContentStoreExportParams params) {
		switch (params.getPathType()) {
		case CONTENTSTORE: 
			String contentUrl = contentData.getContentUrl(); 
			//on remplace "/store://" par "/contentstore"
			if (contentUrl.startsWith(STORE_PREFIX)) {
				contentUrl = contentUrl.substring(STORE_PREFIX.length());
				contentUrl = "/contentstore" + contentUrl;
			}
			return contentUrl;
		case ALFRESCO: 
			return contentUrl = nodeService.getPath(nodeRef).toDisplayPath(nodeService, permissionService)
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
	
	public void setDescriptorService(DescriptorService descriptorService) {
		this.descriptorService = descriptorService;
	}
	
	public void setServiceRegistry(ServiceRegistry serviceRegistry) {
		this.nodeService = serviceRegistry.getNodeService();
		this.searchService = serviceRegistry.getSearchService();
		this.contentService = serviceRegistry.getContentService();
		this.permissionService = serviceRegistry.getPermissionService();
		this.namespacePrefixResolver = serviceRegistry.getNamespaceService();
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
}
