package fr.openwide.alfresco.component.model.search.model.restriction;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;

import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.repository.model.RnModel;

public class RestrictionBuilderTest {
	
	@Test
	public void testGenerateFTS() throws Exception {
		assertEquals(
				"=cm\\:name:\"toto\"\n" +
				"AND NOT ASPECT:cm\\:workingcopy\n" +
				"AND =cm\\:contentPropertyName:\"{http://www.alfresco.org/model/content/1.0}content\"\n" +
				"AND cm\\:modified:<2009-02-14T00\\:31\\:30+01\\:00 TO MAX]\n" +
				"AND cm\\:modified:[MIN TO 2009-02-14T00\\:31\\:30+01\\:00]\n" +
				"AND cm\\:name:<\"abc\" TO \\\\uFFFF]\n" +
				"AND cm\\:name:[\\\\u0000 TO \"def\"]\n" +
				"AND (=cm\\:name:\"titi\"\n" +
				"	OR =cm\\:name:\"tata\")\n" + 
				"AND NOT (=cm\\:name:\"titi\"\n" +
				"	OR =cm\\:name:\"tata\")", 
			new RestrictionBuilder()
				.eq(CmModel.object.name, "toto").of()
				.hasAspect(CmModel.workingCopy).not().of()
				.eq(RnModel.thumbnail.contentPropertyName, CmModel.content.content.getNameReference()).of()
				.gt(CmModel.auditable.modified, new Date(1234567890123L)).of()
				.le(CmModel.auditable.modified, new Date(1234567890123L)).of()
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
				.toQuery());
	}
}
