package fr.openwide.alfresco.component.model.search.model.restriction;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
//import fr.openwide.alfresco.demo.business.model.DemoModel;

public class RestrictionBuilderTest {
	
	@Test
	public void testGenerateFTS() throws Exception {
		assertEquals(
				"PATH:\"/app:company_home/st:sites/cm:swsdp/cm:documentLibrary//.\"\n" +
				"AND PARENT:workspace\\://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c\n" +
				"AND =cm\\:name:\"toto\"\n" +
				"AND =sys\\:node\\-dbid:123\n" +
				"AND =cm\\:name:\"titi*\"\n" +
				"AND @cm\\:name:\"titi\"~0.9\n" +
				"AND NOT ASPECT:cm\\:workingcopy\n" +
				"AND ID:workspace\\://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c\n" +
				"AND =cm\\:contentPropertyName:\"{http://www.alfresco.org/model/content/1.0}content\"\n" +
				"AND @cm\\:modified:2009-02-13T23:31:30.123Z\n" +
				"AND cm\\:modified:<2009-02-13T23:31:30.123Z TO MAX]\n" +
				"AND cm\\:modified:[MIN TO 2009-02-13T23:31:30.123Z]\n" +
				"AND cm\\:name:<\"abc\" TO \\\\uFFFF]\n" +
				"AND cm\\:name:[\\\\u0000 TO \"def\"]\n" +
				"AND SITE:\"swsdp\"\n" +
				"AND FINGERPRINT:aeb883c2-ad52-43f1-ab9f-1bf16137e79c_20_80\n" +
				"AND (=cm\\:name:\"titi\"\n" +
				"	OR =cm\\:name:\"tata\")\n" + 
				"AND NOT (=cm\\:name:\"titi\"\n" +
				"	OR =cm\\:name:\"tata\")", 
			new RestrictionBuilder()
				.path("/app:company_home/st:sites/cm:swsdp/cm:documentLibrary").orBelow().of()
				.parent(NodeReference.create("workspace://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c")).of()
				.eq(CmModel.object.name, "toto").of()
				.eq(SysModel.referenceable.nodeDbid, 123L).of()
				.startsWith(CmModel.object.name, "titi").of()
				.match(CmModel.object.name, "titi").fuzzy(0.9).of()
				.hasAspect(CmModel.workingCopy).not().of()
				.id(NodeReference.create("workspace://SpacesStore/aeb883c2-ad52-43f1-ab9f-1bf16137e79c")).of()
				.eq(RnModel.thumbnail.contentPropertyName, CmModel.content.content.getNameReference()).of()
				.eq(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.gt(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.le(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.gt(CmModel.object.name, "abc").of()
				.le(CmModel.object.name, "def").of()
				.site("swsdp").of()
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
				"AND o.cmis:lastModificationDate > TIMESTAMP '2009-02-13T23:31:30.123Z'\n" +
				"AND o.cmis:lastModificationDate <= TIMESTAMP '2009-02-13T23:31:30.123Z'\n" +
				"AND o.cmis:lastModificationDate >= TIMESTAMP '2009-02-13T23:31:30.123Z'\n" +
				"	AND o.cmis:lastModificationDate < TIMESTAMP '2009-02-13T23:31:30.999Z'\n" +
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
