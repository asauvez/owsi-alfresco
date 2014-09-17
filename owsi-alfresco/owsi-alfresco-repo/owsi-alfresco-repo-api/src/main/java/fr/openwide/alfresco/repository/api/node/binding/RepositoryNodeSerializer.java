package fr.openwide.alfresco.repository.api.node.binding;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;


public abstract class RepositoryNodeSerializer {

	public static Map<NameReference, Serializable> toJSon(Map<NameReference, Serializable> map) {
		Map<NameReference, Serializable> mapJson = new LinkedHashMap<NameReference, Serializable>();
		for (Entry<NameReference, Serializable> property : map.entrySet()) {
			if (property.getValue() instanceof Collection) {
				Collection<?> collection = (Collection<?>) property.getValue();
				Iterator<?> it = collection.iterator();
				if (it.hasNext()) {
					Object firstValue = it.next();
					if (firstValue instanceof Date) {
						mapJson.put(property.getKey(), collection.toArray(new Date[collection.size()]));
					} else if (firstValue instanceof Locale) {
						mapJson.put(property.getKey(), collection.toArray(new Locale[collection.size()]));
					} else if (firstValue instanceof Long) {
						mapJson.put(property.getKey(), collection.toArray(new Long[collection.size()]));
					} else if (firstValue instanceof Float) {
						mapJson.put(property.getKey(), collection.toArray(new Float[collection.size()]));
					} else if (firstValue instanceof Double) {
						mapJson.put(property.getKey(), collection.toArray(new Double[collection.size()]));
					} else if (firstValue instanceof NameReference) {
						mapJson.put(property.getKey(), collection.toArray(new NameReference[collection.size()]));
					} else if (firstValue instanceof NodeReference) {
						mapJson.put(property.getKey(), collection.toArray(new NodeReference[collection.size()]));
					} else if (collection instanceof ArrayList) {
						mapJson.put(property.getKey(), (ArrayList<?>) collection);
					} else {
						mapJson.put(property.getKey(), new ArrayList<Object>(collection));
					}
				}
			} else {
				mapJson.put(property.getKey(), property.getValue());
			}
		}
		return mapJson;
	}
	
	public static void toNative(Map<NameReference, Serializable> mapNative, Map<NameReference, Serializable> mapJson) {
		mapNative.clear();
		for (Entry<NameReference, Serializable> property : mapJson.entrySet()) {
			if (property.getValue() instanceof Date[]) {
				mapNative.put(property.getKey(), (Serializable) Arrays.asList((Date[]) property.getValue()));
			} else if (property.getValue() instanceof Locale[]) {
				mapNative.put(property.getKey(), (Serializable) Arrays.asList((Locale[]) property.getValue()));
			} else if (property.getValue() instanceof Long[]) {
				mapNative.put(property.getKey(), (Serializable) Arrays.asList((Long[]) property.getValue()));
			} else if (property.getValue() instanceof Float[]) {
				mapNative.put(property.getKey(), (Serializable) Arrays.asList((Float[]) property.getValue()));
			} else if (property.getValue() instanceof Double[]) {
				mapNative.put(property.getKey(), (Serializable) Arrays.asList((Double[]) property.getValue()));
			} else if (property.getValue() instanceof NameReference[]) {
				mapNative.put(property.getKey(), (Serializable) Arrays.asList((NameReference[]) property.getValue()));
			} else if (property.getValue() instanceof NodeReference[]) {
				mapNative.put(property.getKey(), (Serializable) Arrays.asList((NodeReference[]) property.getValue()));
			} else {
				mapNative.put(property.getKey(), property.getValue());
			}
		}
	}

}
