package fr.openwide.alfresco.repository.api.node.model;

import java.io.IOException;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class RepositoryNodeTest {

	@Test
	public void testSerialization() throws IOException {
		
		ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
		
		RepositoryNode node = new RepositoryNode();
		
		node.getProperties().put(NameReference.create("cm:multiDate"), (Serializable) Arrays.asList(new Date(), new Date()));
		node.getProperties().put(NameReference.create("cm:multiString"), (Serializable) Arrays.asList("abc", "def"));
		node.getProperties().put(NameReference.create("cm:multiName"), (Serializable) Arrays.asList(NameReference.create("cm:a"), NameReference.create("cm:b")));
		node.getProperties().put(NameReference.create("cm:multiRef"), (Serializable) Arrays.asList(NodeReference.create("w://dfga"), NodeReference.create("w://dfgb")));

		node.getProperties().put(NameReference.create("cm:qname"), NameReference.create("cm:toto"));
		node.getProperties().put(NameReference.create("cm:nodeRef"), NodeReference.create("w://dfg"));
		node.getProperties().put(NameReference.create("cm:int"), 5);
		node.getProperties().put(NameReference.create("cm:float"), 5F);
		node.getProperties().put(NameReference.create("cm:double"), 7D);
		node.getProperties().put(NameReference.create("cm:long"), 6L);
		node.getProperties().put(NameReference.create("cm:boolean"), true);
		node.getProperties().put(NameReference.create("cm:content"), new RepositoryContentData("text/plain", "Text", 123L, StandardCharsets.UTF_8.name(), Locale.FRANCE));
		node.getProperties().put(NameReference.create("cm:s"), "ABC");
		node.getProperties().put(NameReference.create("cm:date"), new Date());
		String s = objectMapper.writeValueAsString(node);
		System.out.println(s);
		
		node = objectMapper.readValue(s, RepositoryNode.class);
		System.out.println(node.getProperties());
		
		List<?> multiDate = (List<?>) node.getProperties().get("cm:multiDate");
		assert multiDate.get(0) instanceof Date;
		assert node.getProperties().get("cm:double") instanceof Double;
		assert node.getProperties().get("cm:float") instanceof Float;
		assert node.getProperties().get("cm:int") instanceof Integer;
	}
}
