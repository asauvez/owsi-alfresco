package fr.openwide.alfresco.demo.core.test;

import org.junit.Assert;
import org.junit.Test;

import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.model.OwsiModel;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.demo.business.model.DemoModel;

public class ClassificationTest extends AbstractDemoTest {

	@Test
	public void testClassification() {
		NodeReference rootFolder = getRootFolder();
		
		// Create
		// On n'a pas besoin de passer de parent.
		NodeReference demoFile = nodeModelService.create(new BusinessNode(null, CmModel.content, "demo.txt")
			.properties().set(CmModel.content.content, new RepositoryContentData("text/plain", "UTF-8"))
			.properties().set(DemoModel.demoAspect.demoProperty, "category1")
			.aspect(OwsiModel.classifiable)
			.contents().set("hello world"));
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.path();
		nodeScopeBuilder.assocs().primaryParent()
			.assocs().recursivePrimaryParent()
			.properties().name();
		BusinessNode demoNode = nodeModelService.get(demoFile, nodeScopeBuilder);
		
		String path = demoNode.getPath();
		Assert.assertTrue(path.startsWith("/{http://www.alfresco.org/model/application/1.0}company_home"
				+ "/{http://www.alfresco.org/model/content/1.0}Demo"
				+ "/{http://www.alfresco.org/model/content/1.0}classification"
				+ "/{http://www.alfresco.org/model/content/1.0}category1"
				+ "/{http://www.alfresco.org/model/content/1.0}admin"));
		Assert.assertTrue(path.endsWith("/{http://www.alfresco.org/model/content/1.0}demo.txt"));
		
		BusinessNode parent = demoNode.assocs().primaryParent();
		//Assert.assertEquals("05", parent.properties().getName());
		parent = parent.assocs().primaryParent();
		//Assert.assertEquals("2016", parent.properties().getName());
		parent = parent.assocs().primaryParent();
		Assert.assertEquals("admin", parent.properties().getName());
		parent = parent.assocs().primaryParent();
		Assert.assertEquals("category1", parent.properties().getName());
		parent = parent.assocs().primaryParent();
		Assert.assertEquals("classification", parent.properties().getName());
		parent = parent.assocs().primaryParent();
		Assert.assertEquals("Demo", parent.properties().getName());
		
		nodeModelService.delete(demoFile);
		Assert.assertEquals(0, nodeModelService.getChildren(rootFolder, new NodeScopeBuilder()).size());
	}
	
	@Test
	public void testDeleteIfEmpty() {
		NodeReference rootFolder = getRootFolder();
		
		NodeReference folder = nodeModelService.createFolder(rootFolder, "folder");
		nodeModelService.update(
				new BusinessNode(folder).aspect(OwsiModel.deleteIfEmpty),
				new NodeScopeBuilder().aspect(OwsiModel.deleteIfEmpty));

		NodeReference file1 = nodeModelService.createContent(folder, "demo1.txt", "text/plain", "UTF-8", "hello world");
		NodeReference file2 = nodeModelService.createContent(folder, "demo2.txt", "text/plain", "UTF-8", "hello world");
		Assert.assertEquals(1, nodeModelService.getChildren(rootFolder, new NodeScopeBuilder()).size());

		nodeModelService.delete(file1);
		Assert.assertEquals(1, nodeModelService.getChildren(rootFolder, new NodeScopeBuilder()).size());
		
		nodeModelService.delete(file2);
		Assert.assertEquals(0, nodeModelService.getChildren(rootFolder, new NodeScopeBuilder()).size());
	}
}
