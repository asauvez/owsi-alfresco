package fr.openwide.alfresco.repo.module.bootstrap.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.importer.ImporterBootstrap;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.CategoryService;
import org.alfresco.service.cmr.security.AuthorityService;
import org.alfresco.service.cmr.security.AuthorityType;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.namespace.QName;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.repo.dictionary.node.service.impl.NodeModelRepositoryServiceImpl;
import fr.openwide.alfresco.repo.module.bootstrap.service.BootstrapService;
import fr.openwide.alfresco.repo.module.classification.service.impl.ClassificationServiceImpl;
import fr.openwide.alfresco.repository.remote.conversion.service.ConversionService;


public class BootstrapServiceImpl implements BootstrapService {

	private final Logger logger = LoggerFactory.getLogger(ClassificationServiceImpl.class);
	
	private NodeModelRepositoryServiceImpl nodeModelService;
	private ConversionService conversionService;
	
	private MutableAuthenticationService authenticationService;
	private PersonService personService;
	private AuthorityService authorityService;
	private CategoryService categoryService;
	private ImporterBootstrap importerBootstrap;

	@Override
	public RepositoryAuthority createGroup(RepositoryAuthority group, RepositoryAuthority ... parentAuthorities) {
		logger.debug("Create group " + group);
		
		authorityService.createAuthority(AuthorityType.GROUP, group.getGroupShortName());
		addAuthority(parentAuthorities, group);
		return group;
	}
	@Override
	public RepositoryAuthority createGroup(String groupName, RepositoryAuthority ... parentAuthorities) {
		return createGroup(RepositoryAuthority.group(groupName), parentAuthorities);
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
							list.add(RepositoryAuthority.GROUP_PREFIX + parentAuthority.trim());
						}
					}
					RepositoryAuthority authority = createGroup(groupName);
					authorityService.addAuthority(list, authority.getName());
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	@Override
	public RepositoryAuthority createTestUser(String username, RepositoryAuthority ... parentAuthorities) {
		return createUser(username, "Test", username, username + "@test.fr", username, parentAuthorities);
	}
	
	@Override
	public RepositoryAuthority createUser(String username, String firstName, String lastName, String email, String password, RepositoryAuthority ... parentAuthorities) {
		logger.debug("Create user " + username);
		
		Map<QName, Serializable> user = new HashMap<QName, Serializable>();
		user.put(ContentModel.PROP_USERNAME, username);
		user.put(ContentModel.PROP_FIRSTNAME, firstName);
		user.put(ContentModel.PROP_LASTNAME, lastName);
		user.put(ContentModel.PROP_EMAIL, email);
		personService.createPerson(user);
		
		authenticationService.createAuthentication(username, password.toCharArray());
		
		RepositoryAuthority authority = RepositoryAuthority.user(username);
		addAuthority(parentAuthorities, authority);
		return authority;
	}
	
	private void addAuthority(RepositoryAuthority[] parentAuthorities, RepositoryAuthority authority) {
		if (parentAuthorities.length > 0) {
			List<String> list = new ArrayList<>();
			for (RepositoryAuthority parentAuthority : parentAuthorities) {
				list.add(parentAuthority.getName());
			}
			authorityService.addAuthority(list, authority.getName());
		}
	}

	@Override
	public NodeReference createRootCategory(String categoryName) {
		logger.debug("Create root category " + categoryName);
		
		return conversionService.get(categoryService.createRootCategory(
				StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, 
				ContentModel.ASPECT_GEN_CLASSIFIABLE, 
				categoryName));
	}

	@Override
	public NodeReference createCategory(NodeReference parentCategory, String categoryName) {
		logger.debug("Create category " + categoryName);
		
		return conversionService.get(categoryService.createCategory(conversionService.getRequired(parentCategory), categoryName));
	}

	@Override
	public NodeReference importFileFromClassPath(NodeReference parentRef, String fileName) {
		try (InputStream content = getClass().getClassLoader().getResourceAsStream(fileName)) {
			return nodeModelService.create(new BusinessNode(parentRef, CmModel.content, FilenameUtils.getName(fileName))
					.contents().set(content));
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	@Override
	public void importView(NodeReference parentRef, String viewFileName, String messageFileName) {
		logger.debug("Import view "  + viewFileName);
		
		String path = nodeModelService.getPath(parentRef);
		
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

	// --- Injections ------------------------------------------------------------------------------------------------

	public void setNodeModelService(NodeModelRepositoryServiceImpl nodeModelService) {
		this.nodeModelService = nodeModelService;
	}
	public void setConversionService(ConversionService conversionService) {
		this.conversionService = conversionService;
	}
	public void setAuthenticationService(MutableAuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}
	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}
	public void setAuthorityService(AuthorityService authorityService) {
		this.authorityService = authorityService;
	}
	public void setCategoryService(CategoryService categoryService) {
		this.categoryService = categoryService;
	}
	public void setImporterBootstrap(ImporterBootstrap importerBootstrap) {
		this.importerBootstrap = importerBootstrap;
	}

}
