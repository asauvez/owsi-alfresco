package sco.demo.business.model.demo;

import static org.junit.Assert.*;
import org.junit.Test;

import fr.openwide.alfresco.component.model.node.model.ModelChecker;
import fr.openwide.alfresco.demo.business.model.DemoModel;

public class ModelCheckerTest {
	
	@Test
	public void testModel() throws Exception {
		ModelChecker checker = new ModelChecker();
		checker.checkModel(DemoModel.class);
				
		assertEquals("", checker.getErrors());
	}
}
