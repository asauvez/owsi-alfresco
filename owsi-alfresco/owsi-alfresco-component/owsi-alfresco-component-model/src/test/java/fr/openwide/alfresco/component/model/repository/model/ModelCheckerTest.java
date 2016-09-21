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
		checker.checkModel(EmailServerModel.class);
				
		assertEquals(
				  "SysReferenceable : Field name storeProtocol is not the same as the property name store-protocol\n"
				+ "SysReferenceable : Field name storeIdentifier is not the same as the property name store-identifier\n"
				+ "SysReferenceable : Field name nodeUuid is not the same as the property name node-uuid\n"
				+ "SysReferenceable : Field name nodeDbid is not the same as the property name node-dbid\n"
				+ "CmModel : Field name copiedFrom is not the same as the container name copiedfrom\n"
				+ "CmModel : Field name workingCopy is not the same as the container name workingcopy\n"
				+ "CmModel : Field name generalClassifiable is not the same as the container name generalclassifiable\n"
				+ "CmEmailed : Field name subjectLine is not the same as the property name subjectline\n"
				+ "CmEmailed : Field name sentDate is not the same as the property name sentdate\n"
				+ "CmModel : Field name object is not the same as the container name cmobject\n"
				+ "CmModel : Field name categoryRoot is not the same as the container name category_root\n"
				+ "StModel : Field name customSiteProperties is not the same as the container name customsiteproperties\n"
				+ "StCustomSiteProperties : Field NAMESPACE st is not the same as the property namespace stcp:additionalInformation\n"
				+ "RnModel : Field NAMESPACE rn is not the same as the container namespace cm:thumbnail\n"
				+ "CmThumbnail : Field NAMESPACE rn is not the same as the property namespace cm:thumbnailName\n"
				+ "CmThumbnail : Field NAMESPACE rn is not the same as the property namespace cm:contentPropertyName", 
			checker.getErrors());
	}
}
