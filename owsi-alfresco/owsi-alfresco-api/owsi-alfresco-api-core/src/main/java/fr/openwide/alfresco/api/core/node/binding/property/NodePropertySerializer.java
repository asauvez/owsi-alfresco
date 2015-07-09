package fr.openwide.alfresco.api.core.node.binding.property;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Locale;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import fr.openwide.alfresco.api.core.node.model.RepositoryContentData;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.core.remote.model.StoreReference;

public class NodePropertySerializer extends JsonSerializer<Serializable> 
	implements NodePropertySerializerConstants {

	@Override
	public void serialize(Serializable value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value == null) {
			jgen.writeNull();
		} else if (value instanceof String) {
			jgen.writeString((String) value);
		} else if (value instanceof Boolean) {
			jgen.writeBoolean((Boolean) value);
		} else if (value instanceof Integer) {
			jgen.writeNumber((Integer) value);
		} else if (value instanceof Short) {
			jgen.writeStartObject();
			jgen.writeFieldName(SHORT);
			jgen.writeNumber((Short) value);
			jgen.writeEndObject();
		} else if (value instanceof Long) {
			jgen.writeStartObject();
			jgen.writeFieldName(LONG);
			jgen.writeNumber((Long) value);
			jgen.writeEndObject();
		} else if (value instanceof Float) {
			jgen.writeNumber((Float) value);
		} else if (value instanceof Double) {
			jgen.writeStartObject();
			jgen.writeFieldName(DOUBLE);
			jgen.writeNumber((Double) value);
			jgen.writeEndObject();
		} else if (value instanceof Date) {
			writeStringObject(jgen, DATE, DATE_FORMAT.format(value));
		} else if (value instanceof Locale) {
			writeStringObject(jgen, LOCALE, ((Locale) value).toLanguageTag());
		} else if (value instanceof Collection) {
			jgen.writeStartArray();
			for (Object o : ((Collection<?>) value)) {
				serialize((Serializable) o, jgen, provider);
			}
			jgen.writeEndArray();
		} else if (value instanceof NameReference) {
			writeStringObject(jgen, NAME_REFERENCE, ((NameReference) value).getFullName());
		} else if (value instanceof NodeReference) {
			writeStringObject(jgen, NODE_REFERENCE, ((NodeReference) value).getReference());
		} else if (value instanceof StoreReference) {
			writeStringObject(jgen, STORE_REFERENCE, ((StoreReference) value).getReference());
		} else if (value instanceof RepositoryContentData) {
			jgen.writeStartObject();
			jgen.writeFieldName(CONTENT_DATA);
			jgen.writeObject(value);
			jgen.writeEndObject();
		} else {
			throw new IllegalStateException(value.getClass().getName());
		}
	}
	
	private void writeStringObject(JsonGenerator jgen, String fieldName, String value) throws IOException {
		jgen.writeStartObject();
		jgen.writeFieldName(fieldName);
		jgen.writeString(value);
		jgen.writeEndObject();
	}

}
