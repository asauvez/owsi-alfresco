package fr.openwide.alfresco.demo.core.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.component.model.authority.model.AuthorityQueryBuilder;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class AuthorityTest extends AbstractDemoTest {

	@Autowired
	private AuthorityModelService authorityModelService;
	
	@Test
	public void testUser() {
		authorityModelService.createUser("jsnow", "John", "Snow", "john.snow@watch.org", "ygritte");
		
		Assert.assertEquals("jsnow", authorityModelService.getUser("jsnow", new NodeScopeBuilder()
				.properties().set(CmModel.person.userName)).properties().get(CmModel.person.userName));
		
		authorityModelService.deleteUser(RepositoryAuthority.user("jsnow"));
	}

	@Test
	public void testGroup() {
		RepositoryAuthority group = RepositoryAuthority.group("watchmen");
		authorityModelService.createRootGroup(group, "The watch");
		
		Assert.assertEquals("The watch", 
				authorityModelService.getGroup(group, 
					new NodeScopeBuilder().properties().set(CmModel.authorityContainer.authorityDisplayName))
					.properties().get(CmModel.authorityContainer.authorityDisplayName));
		
		Assert.assertEquals(0,
				authorityModelService.getContainedUsers(new AuthorityQueryBuilder()
					.parentAuthority(group)).size());
		
		authorityModelService.createUser("jsnow", "John", "Snow", "john.snow@watch.org", "ygritte");
		authorityModelService.addToGroup(RepositoryAuthority.user("jsnow"), group);
		Assert.assertEquals(1,
				authorityModelService.getContainedUsers(new AuthorityQueryBuilder()
					.parentAuthority(group)).size());

		authorityModelService.removeFromGroup(RepositoryAuthority.user("jsnow"), group);
		Assert.assertEquals(0,
				authorityModelService.getContainedUsers(new AuthorityQueryBuilder()
					.parentAuthority(group)).size());
		
		authorityModelService.deleteUser(RepositoryAuthority.user("jsnow"));
		
		authorityModelService.deleteGroup(group);
	}

	@Test
	public void testGetContainedUsers() {
		List<BusinessNode> users = authorityModelService.getContainedUsers(new AuthorityQueryBuilder()
				.parentAuthority(RepositoryAuthority.GROUP_ADMINISTRATORS)
				.nodeScopeBuilder(new NodeScopeBuilder()
					.properties().set(CmModel.person.userName)));
		boolean foundAdmin = false;
		for (BusinessNode node : users) {
			if ("admin".equals(node.properties().get(CmModel.person.userName))) {
				foundAdmin = true;
			}
		}
		if (! foundAdmin) {
			Assert.fail("No admin found");
		}
	}
}
