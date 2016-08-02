package fr.openwide.alfresco.demo.core.test;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Optional;

import fr.openwide.alfresco.api.core.authority.exception.AuthorityExistsRemoteException;
import fr.openwide.alfresco.api.core.authority.model.RepositoryAuthority;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.site.model.SiteReference;
import fr.openwide.alfresco.app.core.site.model.RepositorySite;
import fr.openwide.alfresco.app.core.site.service.SiteService;
import fr.openwide.alfresco.component.model.authority.service.AuthorityModelService;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;

public class SiteIT extends AbstractDemoIT { 
	
	@Autowired
	private NodeSearchModelService nodeSearchModelService;
	
	@Autowired
	private SiteService siteService;
	@Autowired
	private AuthorityModelService authorityModelService;
	
	@Test
	public void testRegexSite(){
		RepositorySite rs = new RepositorySite("  Ar  ^géèà0-1!  ");
		Assert.assertEquals("ar-g0-1", rs.getShortName());
	}

	@Test
	public void testSite() {
		String siteName = "site_it_" + System.currentTimeMillis();
//		String i = new RestrictionBuilder()
//			.isType(StModel.site).of()
//			.eq(StModel.site.name, siteName).of()
//			.toFtsQuery();
//		System.out.println(i);

		
		//Create 
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.properties().set(CmModel.object.name)
				.contents().asString();

		List<BusinessNode> listBefore = nodeSearchModelService.search(new RestrictionBuilder()
				.eq(CmModel.object.name, siteName).of()
				.isType(StModel.site).of(),
				nodeScopeBuilder);
		Assert.assertEquals(0, listBefore.size());

		RepositorySite site = new RepositorySite("SiteIT", siteName);
		SiteReference siteReference = siteService.createSite(site);
		
		List<BusinessNode> list = nodeSearchModelService.search(new RestrictionBuilder()
				.eq(CmModel.object.name, siteName).of()
				.isType(StModel.site).of(),
				nodeScopeBuilder);
		Assert.assertEquals(1, list.size());

		Optional<NodeReference> nodeSite = siteService.getSiteNodeReference(siteReference);
		Assert.assertTrue(nodeSite.isPresent());

		try{
			authorityModelService.createUser("jsnow", "John", "Snow", "john.snow@watch.org", "ygritte");
		} catch (AuthorityExistsRemoteException aere) {
			authorityModelService.deleteUser(RepositoryAuthority.user("jsnow"));
			authorityModelService.createUser("jsnow", "John", "Snow", "john.snow@watch.org", "ygritte");
		}
		
		RepositoryAuthority user = RepositoryAuthority.user("jsnow");
		try {
			siteService.addManager(siteReference, user);
			siteService.removeManager(siteReference, user);
		} catch (Exception e){
			Assert.fail("Error during managing manager group");
		}
		
		try {
			siteService.addCollaborator(siteReference, user);
			siteService.removeCollaborator(siteReference, user);
		} catch (Exception e){
			Assert.fail("Error during managing collaborator group");
		}
		
		try {
			siteService.addConsumer(siteReference, user);
			siteService.removeConsumer(siteReference, user);
		} catch (Exception e){
			Assert.fail("Error during managing consumer group");
		}
		try {
			siteService.addContributor(siteReference, user);
			siteService.removeContributor(siteReference, user);
		} catch (Exception e){
			Assert.fail("Error during managing contributor group");
		}
		
		// Delete
		siteService.deleteSite(SiteReference.create(site.getShortName()));
		
		list = nodeSearchModelService.search(new RestrictionBuilder()
				.eq(CmModel.object.name, siteName).of()
				.isType(StModel.site).of(),
				nodeScopeBuilder);
		Assert.assertEquals(0, list.size());

//		Marche toujours, car les groupes correspondants ne sont supprimés que quand le site n'est plus dans la corbeille.
//		try{
//			siteService.addManager(siteReference, user);
//			Assert.fail();
//		}catch (Exception e){
//			//nop
//		}

		authorityModelService.deleteUser(user);
	}

}
