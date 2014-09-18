package fr.openwide.alfresco.repository.api.node.binding;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

import org.springframework.util.Assert;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import fr.openwide.alfresco.repository.api.node.model.RepositoryContentData;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;
import fr.openwide.alfresco.repository.api.remote.model.StoreReference;

public class NodePropertyDeserializer extends JsonDeserializer<Serializable> implements NodePropertySerializerConstants {

	@Override
	public Serializable deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		return deserializeIntern(jp);
	}
	
	public Serializable deserializeIntern(JsonParser jp) throws IOException, JsonProcessingException {
		switch (jp.getCurrentToken()) {
		case START_ARRAY:
			ArrayList<Serializable> list = new ArrayList<Serializable>(); 
			while (jp.nextToken() != JsonToken.END_ARRAY) {
				list.add(deserializeIntern(jp));
			}
			return list;
		case VALUE_NULL:
			return null;
		case VALUE_TRUE:
			return Boolean.TRUE;
		case VALUE_FALSE:
			return Boolean.FALSE;
		case VALUE_STRING:
			return jp.readValueAs(String.class);
		case VALUE_NUMBER_INT:
			return jp.readValueAs(Integer.class);
		case VALUE_NUMBER_FLOAT:
			return jp.readValueAs(Float.class);
		case START_OBJECT:
			Serializable res = readJsonObject(jp);
			Assert.isTrue(jp.nextToken() == JsonToken.END_OBJECT);
			return res;
		default:
			throw new IllegalStateException(jp.getCurrentToken().name());
		}
	}
	
	private Serializable readJsonObject(JsonParser jp) throws IOException {
		Assert.isTrue(jp.nextToken() == JsonToken.FIELD_NAME);
		String type = jp.getText();
		jp.nextToken();
		
		switch (type) {
		case DATE:
			try {
				return DATE_FORMAT.parse(jp.getText());
			} catch (ParseException e) {
				throw new IllegalStateException(e);
			}
		case LOCALE:
			return Locale.forLanguageTag(jp.getText());
		case SHORT:
			return jp.readValueAs(Short.class);
		case LONG:
			return jp.readValueAs(Long.class);
		case DOUBLE:
			return jp.readValueAs(Double.class);
		case NODE_REFERENCE:
			return NodeReference.create(jp.getText());
		case STORE_REFERENCE:
			return StoreReference.create(jp.getText());
		case NAME_REFERENCE:
			return NameReference.create(jp.getText());
		case CONTENT_DATA:
			return jp.readValueAs(RepositoryContentData.class);
		default:
			throw new IllegalStateException(jp.getCurrentToken().name());
		}
	}
}
