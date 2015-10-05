package fr.openwide.alfresco.repo.module.bootstrap.service.impl;

import java.io.IOException;
import java.io.InputStream;
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
	public RepositoryAuthority createGroup(RepositoryAuthority group, RepositoryAuthority ... parentGroups) {
		logger.debug("Create group " + group);
		
		authorityService.createAuthority(AuthorityType.GROUP, group.getGroupShortName());
		
		if (parentGroups.length > 0) {
			authorityService.addAuthority(toAuthorityNames(parentGroups), group.getName());
		}
		
		return group;
	}
	@Override
	public RepositoryAuthority createGroup(String groupName, RepositoryAuthority ... parentGroups) {
		return createGroup(RepositoryAuthority.group(groupName));
	}

	@Override
	public RepositoryAuthority createTestUser(String username, RepositoryAuthority ... parentGroups) {
		return createUser(username, "Test", username, username + "@test.fr", username);
	}
	
	@Override
	public RepositoryAuthority createUser(String username, String firstName, String lastName, String email, String password, RepositoryAuthority ... parentGroups) {
		logger.debug("Create user " + username);
		
		Map<QName, Serializable> user = new HashMap<QName, Serializable>();
		user.put(ContentModel.PROP_USERNAME, username);
		user.put(ContentModel.PROP_FIRSTNAME, firstName);
		user.put(ContentModel.PROP_LASTNAME, lastName);
		user.put(ContentModel.PROP_EMAIL, email);
		personService.createPerson(user);
		
		authenticationService.createAuthentication(username, password.toCharArray());
		
		if (parentGroups.length > 0) {
			authorityService.addAuthority(toAuthorityNames(parentGroups), username);
		}
		
		return RepositoryAuthority.user(username);
	}
	
	private List<String> toAuthorityNames(RepositoryAuthority[] authorities) {
		List<String> list = new ArrayList<>();
		for (RepositoryAuthority authority : authorities) {
			list.add(authority.getName());
		}
		return list;
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
	public void importView(NodeReference parentRef, String fileName) {
		logger.debug("Import view "  + fileName);
		
		String path = nodeModelService.getPath(parentRef);
		
		List<Properties> bootstrapViews = new ArrayList<Properties>(1);
		Properties bootstrapView = new Properties();
		bootstrapView.setProperty(ImporterBootstrap.VIEW_PATH_PROPERTY, path);
		bootstrapView.setProperty(ImporterBootstrap.VIEW_LOCATION_VIEW, fileName);
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
