package fr.openwide.alfresco.repo.core.bootstrap.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.repo.site.SiteModel;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import fr.openwide.alfresco.api.core.authority.model.AuthorityReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.DlModel;
import fr.openwide.alfresco.component.model.repository.model.dl.DlDataListItem;
import fr.openwide.alfresco.repo.core.bootstrap.service.BootstrapService;
import fr.openwide.alfresco.repo.dictionary.node.service.NodeModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;

@GenerateService(
	id="owsi.service.bootstrapService")
public class BootstrapServiceImpl implements BootstrapService {

	private final Logger logger = LoggerFactory.getLogger(BootstrapServiceImpl.class);
	
	@Autowired private NodeModelService nodeModelService;
	@Autowired private NodeModelRepositoryService nodeModelRepositoryService;
	@Autowired private ConversionService conversionService;
	
	@Autowired private MutableAuthenticationService authenticationService;
	@Autowired private PersonService personService;
	@Autowired private AuthorityService authorityService;
	@Autowired private CategoryService categoryService;
	@Autowired private SiteService siteService;
	@Autowired private NodeService nodeService;
	@Autowired private ContentService contentService;
	@Autowired private FileFolderService fileFolderService;
	@Autowired @Qualifier("spacesBootstrap") private ImporterBootstrap importerBootstrap;

	@Override
	public AuthorityReference createGroup(AuthorityReference group, AuthorityReference ... parentAuthorities) {
		logger.debug("Create group " + group);
		
		authorityService.createAuthority(AuthorityType.GROUP, group.getGroupShortName());
		addAuthority(parentAuthorities, group);
		return group;
	}
	@Override
	public AuthorityReference createGroup(String groupName, AuthorityReference ... parentAuthorities) {
		return createGroup(AuthorityReference.group(groupName), parentAuthorities);
	}

	@Override
	public void importGroupsFile(String fileName) {
		try (BufferedReader content = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(fileName)))) {
			String line = content.readLine(); // header
			while ((line = content.readLine()) != null) {
				if (line.trim().length() > 0) {
					String[] split = line.split(",");
					String groupName = split[0].trim();
					//String description = split[1].trim();
					//String OU = split[2].trim();
					
					List<String> list = new ArrayList<>();
					if (split.length > 3) {
						for (String parentAuthority : split[3].trim().split("/")) {
							list.add(AuthorityReference.GROUP_PREFIX + parentAuthority.trim());
						}
					}
					AuthorityReference authority = createGroup(groupName);
					authorityService.addAuthority(list, authority.getName());
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public AuthorityReference createTestUser(String username, AuthorityReference ... parentAuthorities) {
		return createUser(username, "Test", username, username + "@test.fr", username, parentAuthorities);
	}
	
	@Override
	public AuthorityReference createUser(String username, String firstName, String lastName, String email, String password, AuthorityReference ... parentAuthorities) {
		logger.debug("Create user " + username);
		
		Map<QName, Serializable> user = new HashMap<QName, Serializable>();
		user.put(ContentModel.PROP_USERNAME, username);
		user.put(ContentModel.PROP_FIRSTNAME, firstName);
		user.put(ContentModel.PROP_LASTNAME, lastName);
		user.put(ContentModel.PROP_EMAIL, email);
		personService.createPerson(user);
		
		authenticationService.createAuthentication(username, password.toCharArray());
		
		AuthorityReference authority = AuthorityReference.user(username);
		addAuthority(parentAuthorities, authority);
		return authority;
	}
	
	private void addAuthority(AuthorityReference[] parentAuthorities, AuthorityReference authority) {
		if (parentAuthorities.length > 0) {
			List<String> list = new ArrayList<>();
			for (AuthorityReference parentAuthority : parentAuthorities) {
				list.add(parentAuthority.getName());
			}
			authorityService.addAuthority(list, authority.getName());
		}
	}

	@Override
	public NodeRef createRootCategory(String categoryName) {
		logger.debug("Create root category " + categoryName);
		
		return categoryService.createRootCategory(
				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
				ContentModel.ASPECT_GEN_CLASSIFIABLE, 
				categoryName);
	}

	@Override
	public NodeRef createCategory(NodeRef parentCategory, String categoryName) {
		logger.debug("Create category " + categoryName);
		
		return categoryService.createCategory(parentCategory, categoryName);
	}

	@Override
	public NodeRef importFileFromClassPath(NodeRef parentRef, String fileName) {
		try (InputStream content = getClass().getClassLoader().getResourceAsStream(fileName)) {
			return conversionService.getRequired(nodeModelService.create(
					new BusinessNode(conversionService.get(parentRef), CmModel.content, 
							FilenameUtils.getName(fileName))
					.contents().set(content)));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public void importView(NodeRef parentRef, String viewFileName, String messageFileName) {
		logger.debug("Import view "  + viewFileName);
		
		String path = nodeModelRepositoryService.getPath(parentRef);
		
		List<Properties> bootstrapViews = new ArrayList<Properties>(1);
		Properties bootstrapView = new Properties();
		bootstrapView.setProperty(ImporterBootstrap.VIEW_PATH_PROPERTY, path);
		bootstrapView.setProperty(ImporterBootstrap.VIEW_LOCATION_VIEW, viewFileName);
		bootstrapView.setProperty(ImporterBootstrap.VIEW_MESSAGES_PROPERTY, messageFileName);
		bootstrapView.setProperty(ImporterBootstrap.VIEW_ENCODING, "UTF-8");
		
		bootstrapViews.add(bootstrapView);
		importerBootstrap.setUseExistingStore(true);
		importerBootstrap.bootstrap();
	}
	
	@Override
	public SiteInfo createSite(String siteName, String siteTitle, String siteDescription, SiteVisibility siteVisibility) {
		SiteInfo siteInfo = siteService.getSite(siteName);
		if (siteInfo == null) {
			siteInfo = siteService.createSite("site-dashboard", siteName, siteTitle, siteDescription, siteVisibility);
			// TODO siteService.setMembership(siteInfo.getShortName(), "admin", SiteRole.SiteManager.toString());
			createDefaultDashboard(siteInfo);
			siteService.createContainer(siteInfo.getShortName(), SiteService.DOCUMENT_LIBRARY, null, null);
		}
		return siteInfo;
	}
	@Override
	public NodeRef createDataListContainer(SiteInfo siteInfo) {
		return siteService.createContainer(siteInfo.getShortName(), "dataLists", null, 
				Collections.singletonMap(SiteModel.PROP_COMPONENT_ID, "dataLists"));
	}
	@Override
	public NodeRef createDataList(NodeRef dataListContainer, String name, DlDataListItem dataListItemType) {
		NodeRef dataList = nodeModelRepositoryService.createNode(dataListContainer, DlModel.dataList, name);
		nodeModelRepositoryService.setProperty(dataList, DlModel.dataList.dataListItemType, dataListItemType.getNameReference().getFullName());
		return dataList;
	}

	private void createDefaultDashboard(SiteInfo siteInfo) {
		FileInfo surfConfig = fileFolderService.create(siteInfo.getNodeRef(), "surf-config", ContentModel.TYPE_FOLDER);
		Map<QName, Serializable> properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_CASCADE_HIDDEN, Boolean.TRUE);
		properties.put(ContentModel.PROP_CASCADE_INDEX_CONTROL, Boolean.TRUE);
		nodeService.addAspect(surfConfig.getNodeRef(), ContentModel.ASPECT_HIDDEN, properties);

		properties = new HashMap<QName, Serializable>();
		properties.put(ContentModel.PROP_IS_INDEXED, Boolean.FALSE);
		properties.put(ContentModel.PROP_IS_CONTENT_INDEXED, Boolean.FALSE);
		nodeService.addAspect(surfConfig.getNodeRef(), ContentModel.ASPECT_INDEX_CONTROL, properties);

		FileInfo pages = fileFolderService.create(surfConfig.getNodeRef(), "pages", ContentModel.TYPE_FOLDER);
		FileInfo site = fileFolderService.create(pages.getNodeRef(), "site", ContentModel.TYPE_FOLDER);
		FileInfo siteName = fileFolderService.create(site.getNodeRef(), siteInfo.getShortName(),
				ContentModel.TYPE_FOLDER);

		Map<QName, Serializable> props = new HashMap<QName, Serializable>(1);
		props.put(ContentModel.PROP_NAME, "dashboard.xml");

		NodeRef node = nodeService.createNode(siteName.getNodeRef(), ContentModel.ASSOC_CONTAINS,
				QName.createQName(NamespaceService.CONTENT_MODEL_1_0_URI, "dashboard.xml"), ContentModel.TYPE_CONTENT,
				props).getChildRef();

		ContentWriter writer = contentService.getWriter(node, ContentModel.PROP_CONTENT, true);
		writer.setMimetype(MimetypeMap.MIMETYPE_XML);
		writer.setEncoding("UTF-8");
		// TODO Create dashboard.xml file by using an external file resource instead of
		// a hand-coded String
		writer.putContent("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<page>\n"
				+ "      <title>Collaboration Site Dashboard</title>\n"
				+ "      <title-id>page.siteDashboard.title</title-id>\n"
				+ "      <description>Collaboration site's dashboard page</description>\n"
				+ "      <description-id>page.siteDashboard.description</description-id>\n"
				+ "      <authentication>user</authentication>\n"
				+ "      <template-instance>dashboard-2-columns-wide-left</template-instance>\n"
				+ "      <properties>\n"
				+ "        <sitePages>[{\"pageId\": \"documentlibrary\"}, {\"pageId\": \"data-lists\"}]</sitePages>\n"
				+ "      <theme/><dashboardSitePage>true</dashboardSitePage></properties>\n"
				+ "    <page-type-id>generic</page-type-id></page>");

	}
}
