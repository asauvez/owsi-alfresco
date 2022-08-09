package fr.openwide.alfresco.component.model.repository.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import fr.openwide.alfresco.component.model.node.model.ModelChecker;

public class ModelCheckerTest {
	
	@Test
	public void testModel() throws Exception {
		ModelChecker checker = new ModelChecker();
		checker.checkModel(SysModel.class);
		checker.checkModel(CmModel.class);
		checker.checkModel(AppModel.class);
		checker.checkModel(StModel.class);
		checker.checkModel(RnModel.class);
		checker.checkModel(EmailserverModel.class);
		
		assertEquals("", checker.getErrors());
	}
}
