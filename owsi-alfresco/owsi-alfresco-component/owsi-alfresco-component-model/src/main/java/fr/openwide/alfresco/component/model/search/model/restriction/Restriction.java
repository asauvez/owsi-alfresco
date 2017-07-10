package fr.openwide.alfresco.component.model.search.model.restriction;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import org.apache.commons.lang3.time.FastDateFormat;

import com.fasterxml.jackson.databind.util.ISO8601Utils;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.AbstractMultiNumberPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiDateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.multi.MultiNameReferencePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.AbstractNumberPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DatePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.DateTimePropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.NameReferencePropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public abstract class Restriction {
	
	private Map<ContainerModel, String> CMIS_TYPES_REPLACEMENT = new HashMap<>();
	{
		CMIS_TYPES_REPLACEMENT.put(CmModel.content, "cmis:document");
		CMIS_TYPES_REPLACEMENT.put(CmModel.folder, "cmis:folder");
	}
	private Map<PropertyModel<?>, String> CMIS_PROPERTIES_REPLACEMENT = new HashMap<>();
	{
		CMIS_PROPERTIES_REPLACEMENT.put(CmModel.object.name, "cmis:name");
		CMIS_PROPERTIES_REPLACEMENT.put(CmModel.auditable.created, "cmis:creationDate");
		CMIS_PROPERTIES_REPLACEMENT.put(CmModel.auditable.modified, "cmis:lastModificationDate");
		CMIS_PROPERTIES_REPLACEMENT.put(CmModel.auditable.creator, "cmis:createdBy");
		CMIS_PROPERTIES_REPLACEMENT.put(CmModel.auditable.modifier, "cmis:lastModifiedBy");
	}

	private static final FastDateFormat dateFormat = FastDateFormat.getInstance("yyyy-MM-dd");

	private final RestrictionBuilder parent;
	private boolean not = false;

	public Restriction(RestrictionBuilder parent) {
		this.parent = parent;
	}

	public Restriction not() {
		not = true;
		return this;
	}
	protected boolean isNot() {
		return not;
	}

	public RestrictionBuilder of() {
		return parent;
	}
	
	@Override
	public String toString() {
		return toFtsQuery();
	}

	public final String toFtsQuery() {
		String query = toFtsQueryInternal();
		if (query.isEmpty()) return query;
		return (not) 
				? "NOT " + (isNeedingParenthesis() ? "(" + query + ")" : query)  
				: query;
	}
	
	public final String toCmisQueryContent() {
		return toCmisQuery(CmModel.content);
	}
	public final String toCmisQueryFolder() {
		return toCmisQuery(CmModel.folder);
	}
	public final String toCmisQuery(TypeModel type) {
		StringBuilder query = new StringBuilder()
				.append("SELECT o.cmis:objectId FROM ").append(CMIS_TYPES_REPLACEMENT.get(type)).append(" AS o");
		
		Set<ContainerModel> containersToJoin = new LinkedHashSet<>();
		addCmisQueryJoin(containersToJoin);
		containersToJoin.remove(CmModel.auditable);
		
		for (ContainerModel containerToJoin : containersToJoin) {
			String alias = toCmisAlias(containerToJoin);
			query
				.append("\n  JOIN ").append(containerToJoin).append(" AS ").append(alias)
				.append(" ON o.cmis:objectId = ").append(alias).append(".cmis:objectId ");
		}

		String where = toCmisQueryWhereInternal();
		if (! where.isEmpty()) {
			query.append("\nWHERE ").append(where);
		}
		return query.toString();
	}
	
	protected String toCmisAlias(ContainerModel container) {
		return container.getNameReference().toString().replace(':', '_');
	}
	protected String toCmisProperty(PropertyModel<?> property) {
		if (CMIS_PROPERTIES_REPLACEMENT.containsKey(property)) {
			return "o." + CMIS_PROPERTIES_REPLACEMENT.get(property);
		} else {
			return toCmisAlias(property.getType()) + "." + property.getNameReference().getFullName();
		}
	}
	
	public final String toCmisWhereQuery() {
		String query = toCmisQueryWhereInternal();
		if (query.isEmpty()) return query;
		return (not) 
				? "NOT " + (isNeedingParenthesis() ? "(" + query + ")" : query)  
						: query;
	}

	protected boolean isNeedingParenthesis() {
		return false;
	}
	
	protected abstract String toFtsQueryInternal();

	protected void addCmisQueryJoin(@SuppressWarnings("unused") Set<ContainerModel> containersToJoin) {
		// nop
	}
	protected String toCmisQueryWhereInternal() {
		throw new UnsupportedOperationException();
	}
	
	protected static String toFtsLuceneValue(String value) {
		return "\"" + value.toString().replace("\"", "\\\"") + "\"";
	}
	protected static <C extends Serializable> String toFtsLuceneValue(PropertyModel<C> propertyModel, C value) {
		if (propertyModel instanceof DateTimePropertyModel || propertyModel instanceof MultiDateTimePropertyModel) {
			return ISO8601Utils.format((Date) value, false, TimeZone.getDefault()).replace(":", "\\:");
		} else if (propertyModel instanceof DatePropertyModel || propertyModel instanceof MultiDatePropertyModel) {
			return dateFormat.format((Date) value);
		} else if (propertyModel instanceof AbstractNumberPropertyModel || propertyModel instanceof AbstractMultiNumberPropertyModel) {
			return value.toString();
		} else if (propertyModel instanceof NameReferencePropertyModel || propertyModel instanceof MultiNameReferencePropertyModel) {
			return toFtsLuceneValue(((NameReference) value).getFullyQualified());
		} else {
			return toFtsLuceneValue(value.toString());
		}
	}
	
	protected static String toCmisLuceneValue(String value) {
		return "\'" + value.toString().replace("\"", "\\\'") + "\'";
	}
	protected static <C extends Serializable> String toCmisLuceneValue(PropertyModel<C> propertyModel, C value) {
		if (propertyModel instanceof DateTimePropertyModel || propertyModel instanceof MultiDateTimePropertyModel) {
			return "TIMESTAMP '" + ISO8601Utils.format((Date) value, true, TimeZone.getDefault()) + "'";
		} else if (propertyModel instanceof DatePropertyModel || propertyModel instanceof MultiDatePropertyModel) {
			return "TIMESTAMP '" + dateFormat.format((Date) value) + "'";
		} else if (propertyModel instanceof AbstractNumberPropertyModel || propertyModel instanceof AbstractMultiNumberPropertyModel) {
			return value.toString();
		} else if (propertyModel instanceof NameReferencePropertyModel || propertyModel instanceof MultiNameReferencePropertyModel) {
			return toCmisLuceneValue(((NameReference) value).getFullyQualified());
		} else {
			return toCmisLuceneValue(value.toString());
		}
	}

}
