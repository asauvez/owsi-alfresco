package fr.openwide.alfresco.demo.business.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.openwide.alfresco.component.model.node.model.ModelChecker;

public class ModelCheckerTest {
	
	@Test
	public void testModel() throws Exception {
		ModelChecker checker = new ModelChecker();
		checker.checkModel(DemoModel.class);
				
		assertEquals("", checker.getErrors());
	}
}
