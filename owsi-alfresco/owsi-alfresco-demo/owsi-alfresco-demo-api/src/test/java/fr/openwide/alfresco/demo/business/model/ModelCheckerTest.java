package fr.openwide.alfresco.demo.business.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.openwide.alfresco.component.model.node.model.ModelChecker;
import fr.openwide.alfresco.component.model.node.model.XmlModelGenerator;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class ModelCheckerTest {
	
	@Test
	public void testModel() throws Exception {
		System.out.println(new XmlModelGenerator().generateModel(DemoModel.class));
		System.out.println(new XmlModelGenerator().generateModel(CmModel.class));
		
		ModelChecker checker = new ModelChecker();
		checker.checkModel(DemoModel.class);
				
		assertEquals("", checker.getErrors());
	}
}
