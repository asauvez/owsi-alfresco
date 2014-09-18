package fr.openwide.alfresco.repository.api.node.binding;

import java.text.DateFormat;

import com.fasterxml.jackson.databind.util.ISO8601DateFormat;

public interface NodePropertySerializerConstants {

	String DATE = "date";
	DateFormat DATE_FORMAT = new ISO8601DateFormat();
	String SHORT = "short";
	String LONG = "long";
	String DOUBLE = "double";

	String NODE_REFERENCE = "nodeReference";
	String STORE_REFERENCE = "storeReference";
	String NAME_REFERENCE = "nameReference";
	String CONTENT_DATA = "contentData";

}
