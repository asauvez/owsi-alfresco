package fr.openwide.alfresco.component.model.search.model.restriction;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;
//import fr.openwide.alfresco.demo.business.model.DemoModel;

public class RestrictionBuilderTest {
	
	@Test
	public void testGenerateFTS() throws Exception {
		assertEquals(
				"=cm\\:name:\"toto\"\n" +
				"AND =cm\\:name:\"titi*\"\n" +
				"AND NOT ASPECT:cm\\:workingcopy\n" +
				"AND ID:workspace\\://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c\n" +
				"AND =cm\\:contentPropertyName:\"{http://www.alfresco.org/model/content/1.0}content\"\n" +
				"AND cm\\:modified:<2009-02-14T00\\:31\\:30+01\\:00 TO MAX]\n" +
				"AND cm\\:modified:[MIN TO 2009-02-14T00\\:31\\:30+01\\:00]\n" +
				"AND cm\\:name:<\"abc\" TO \\\\uFFFF]\n" +
				"AND cm\\:name:[\\\\u0000 TO \"def\"]\n" +
				"AND FINGERPRINT:aeb883c2-ad52-43f1-ab9f-1bf16137e79c_20_80\n" +
				"AND (=cm\\:name:\"titi\"\n" +
				"	OR =cm\\:name:\"tata\")\n" + 
				"AND NOT (=cm\\:name:\"titi\"\n" +
				"	OR =cm\\:name:\"tata\")", 
			new RestrictionBuilder()
				.eq(CmModel.object.name, "toto").of()
				.startsWith(CmModel.object.name, "titi").of()
				.hasAspect(CmModel.workingCopy).not().of()
				.id(NodeReference.create("workspace://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c")).of()
				.eq(RnModel.thumbnail.contentPropertyName, CmModel.content.content.getNameReference()).of()
				.gt(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.le(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.gt(CmModel.object.name, "abc").of()
				.le(CmModel.object.name, "def").of()
				.fingerPrint(NodeReference.create("workspace://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c"))
					.overlap(20)
					.confident(80).of()
				.or()
					.eq(CmModel.object.name, "titi").of()
					.eq(CmModel.object.name, "tata").of()
					.of()
				.or()
					.eq(CmModel.object.name, "titi").of()
					.eq(CmModel.object.name, "tata").of()
					.not()
					.of()
				.or()
					.of()
				.and()
					.of()
				.toFtsQuery());
	}
	
	@Test
	public void testGenerateCMIS() throws Exception {
		assertEquals(
				"SELECT o.cmis:objectId FROM cmis:document AS o\n" +
				"  JOIN cm:titled AS cm_titled ON o.cmis:objectId = cm_titled.cmis:objectId \n" + 
				"  JOIN cm:cmobject AS cm_cmobject ON o.cmis:objectId = cm_cmobject.cmis:objectId \n" + 
				"  JOIN cm:workingcopy AS cm_workingcopy ON o.cmis:objectId = cm_workingcopy.cmis:objectId \n"+ 
				"WHERE o.cmis:objectId ='workspace://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c'\n" + 
				"AND cm_titled.cm:title='toto'\n" +
				"AND o.cmis:name='toto'\n" +
				"AND NOT o.cmis:name='tata'\n" +
				"AND o.cmis:lastModificationDate > TIMESTAMP '2009-02-14T00:31:30.123+01:00'\n" +
				"AND o.cmis:lastModificationDate <= TIMESTAMP '2009-02-14T00:31:30.123+01:00'\n" +
				"AND o.cmis:lastModificationDate >= TIMESTAMP '2009-02-14T00:31:30.123+01:00'\n" +
				"	AND o.cmis:lastModificationDate < TIMESTAMP '2009-02-14T00:31:30.999+01:00'\n" +
				"AND o.cmis:name > 'abc'\n" +
				"AND o.cmis:name <= 'def'\n" +
				"AND (o.cmis:name='titi'\n" +
				"	OR o.cmis:name='tata')\n" +
				"AND NOT (o.cmis:name='titi'\n" +
				"	OR o.cmis:name='tata')", 
			new RestrictionBuilder()
				.id(NodeReference.create("workspace://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c")).of()
				.eq(CmModel.titled.title, "toto").of()
				.eq(CmModel.object.name, "toto").of()
				.eq(CmModel.object.name, "tata").not().of()
				.hasAspect(CmModel.workingCopy).of()
				.gt(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.le(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.between(CmModel.auditable.modified, new Date(1234567890123L), new Date(1234567890999L))
					.minInclusive(true)
					.maxInclusive(false)
					.of()
				.gt(CmModel.object.name, "abc").of()
				.le(CmModel.object.name, "def").of()
				.or()
					.eq(CmModel.object.name, "titi").of()
					.eq(CmModel.object.name, "tata").of()
					.of()
				.or()
					.eq(CmModel.object.name, "titi").of()
					.eq(CmModel.object.name, "tata").of()
					.not()
					.of()
				.or()
					.of()
				.and()
					.of()
				.toCmisQueryContent());
		
		assertEquals(
				"SELECT o.cmis:objectId FROM cmis:folder AS o", 
			new RestrictionBuilder()
				.toCmisQueryFolder());
	}
}
