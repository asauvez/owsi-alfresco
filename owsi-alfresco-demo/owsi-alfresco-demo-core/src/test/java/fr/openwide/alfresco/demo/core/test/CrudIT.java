package fr.openwide.alfresco.demo.core.test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.demo.business.model.DemoModel;

public class CrudIT extends AbstractDemoIT {

	@Autowired
	private NodeSearchModelService nodeSearchModelService;
	
	@Test
	public void testCRUD() {
		
		NodeReference rootFolder = getRootFolder();
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
			.properties().set(CmModel.object.name)
			.contents().asString();
		
		// Create
		NodeReference demoFile = nodeModelService.createContent(rootFolder, "demo.txt", MediaType.TEXT_PLAIN, StandardCharsets.UTF_8, "hello world");

		// Read
		RestrictionBuilder restriction = new RestrictionBuilder()
			.eq(CmModel.object.name, "demo.txt").of();
		List<BusinessNode> list = nodeSearchModelService.search(
			restriction,
			nodeScopeBuilder);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("demo.txt", list.get(0).properties().get(CmModel.object.name));

		// Read CMIS
		list = nodeSearchModelService.search(new SearchQueryBuilder()
			.restrictionCmisContent(restriction)
			.nodeScopeBuilder(nodeScopeBuilder));
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("demo.txt", list.get(0).properties().get(CmModel.object.name));
		
		list = nodeSearchModelService.search(new SearchQueryBuilder()
				.restrictionCmisFolder(new RestrictionBuilder()
						.eq(CmModel.object.name, "Demo").of())
				.nodeScopeBuilder(nodeScopeBuilder));
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("Demo", list.get(0).properties().get(CmModel.object.name));
		
		
		// get
		BusinessNode node = nodeModelService.get(demoFile, nodeScopeBuilder);
		Assert.assertEquals("demo.txt", node.properties().get(CmModel.object.name));
		Assert.assertEquals("hello world", node.contents().get());
		
		// get children
		list = nodeModelService.getChildren(rootFolder, nodeScopeBuilder);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("demo.txt", list.get(0).properties().get(CmModel.object.name));

		// Update
		nodeModelService.update(new BusinessNode(list.get(0).getNodeReference())
				.properties().set(CmModel.object.name, "demo2.txt"));
		list = nodeSearchModelService.search(
			new RestrictionBuilder()
				.eq(CmModel.object.name, "demo2.txt").of(),
			nodeScopeBuilder);
		Assert.assertEquals(1, list.size());
		Assert.assertEquals("demo2.txt", list.get(0).properties().get(CmModel.object.name));

		// Delete
		nodeModelService.delete(demoFile);
		list = nodeSearchModelService.search(
			new RestrictionBuilder()
				.eq(CmModel.object.name, "demo2.txt").of(),
			nodeScopeBuilder);
		Assert.assertEquals(0, list.size());
	}

	@Test
	public void testMultiUploadDownlod() {
		NodeReference rootFolder = identificationService.getByIdentifier(DemoModel.DEMO_ROOT_FOLDER).get();
		
		// Create multi
		nodeModelService.create(Arrays.asList(
			new BusinessNode(rootFolder, CmModel.content, "demo.txt")
				.properties().set(CmModel.content.content, new RepositoryContentData("text/plain"))
				.contents().set("hello world 1"),
			new BusinessNode(rootFolder, CmModel.content, "demo2.txt")
				.properties().set(CmModel.content.content, new RepositoryContentData("text/plain"))
				.contents().set("hello world 2")));
		
		// Download multi content
		List<BusinessNode> children = nodeModelService.getChildren(rootFolder, new NodeScopeBuilder()
				.contents().asString());
		Assert.assertEquals(2, children.size());
		for (BusinessNode node : children) {
			String content = node.contents().get();
			Assert.assertTrue(content.startsWith("hello world "));
		}
	}
	
	@Test
	public void testNodePropertie(){
		NodeReference rootFolder = getRootFolder();
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
			.properties().set(CmModel.titled.description)
			.properties().set(CmModel.titled.title);
		
		// Create
		NodeReference demoFile = nodeModelService.createContent(rootFolder, "demo.txt", MediaType.TEXT_PLAIN, StandardCharsets.UTF_8, "hello world");
		
		BusinessNode buisNodeTest;
		nodeModelService.update(
			new BusinessNode(demoFile)
				.properties().set(CmModel.titled.description, "demo2"),
			new NodeScopeBuilder()
				.properties().set(CmModel.titled.description));
		buisNodeTest = nodeModelService.get(demoFile, nodeScopeBuilder);
		Assert.assertEquals("demo2", buisNodeTest.properties().get(CmModel.titled.description));

		nodeModelService.update(
			new BusinessNode(demoFile)
				.properties().set(CmModel.titled.description, "")
				.properties().set(CmModel.titled.title, "demo"),
			new NodeScopeBuilder()
				.properties().set(CmModel.titled.description)
				.properties().set(CmModel.titled.title));
		buisNodeTest = nodeModelService.get(demoFile, nodeScopeBuilder);
		Assert.assertEquals("demo", buisNodeTest.properties().get(CmModel.titled.title));
		Assert.assertEquals("", buisNodeTest.properties().get(CmModel.titled.description));

		nodeModelService.update(
			new BusinessNode(demoFile)
				.properties().set(CmModel.titled.description, null),
			new NodeScopeBuilder()
				.properties().set(CmModel.titled.description));
		buisNodeTest = nodeModelService.get(demoFile, nodeScopeBuilder);
		Assert.assertEquals("demo", buisNodeTest.properties().get(CmModel.titled.title));
		Assert.assertEquals(null, buisNodeTest.properties().get(CmModel.titled.description));
		
		nodeModelService.update(
				new BusinessNode(demoFile)
				.properties().set(CmModel.titled.title, "demo test"),
				new NodeScopeBuilder()
				.properties().set(CmModel.titled.title));
		buisNodeTest = nodeModelService.get(demoFile, nodeScopeBuilder);
		Assert.assertEquals("demo test", buisNodeTest.properties().get(CmModel.titled.title));
		
	}
}
