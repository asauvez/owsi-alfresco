package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.property.single.SinglePropertyModel;

public class SubFolderBuilder {
	
	private final NameReference property;
	private Format format = null;
	private String defaultValue = null;

	public SubFolderBuilder(NameReference property) {
		this.property = property;
	}
	public SubFolderBuilder(SinglePropertyModel<?> property) {
		this(property.getNameReference());
	}

	public SubFolderBuilder defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	public SubFolderBuilder format(Format format) {
		this.format = format;
		return this;
	}
	public SubFolderBuilder formatDate(String pattern) {
		return format(new SimpleDateFormat(pattern));
	}
	public SubFolderBuilder formatYear() {
		return formatDate("yyyy");
	}
	public SubFolderBuilder formatMonth() {
		return formatDate("MM");
	}
	public SubFolderBuilder formatDay() {
		return formatDate("dd");
	}
	public SubFolderBuilder formatHour() {
		return formatDate("HH");
	}
	public SubFolderBuilder formatMinute() {
		return formatDate("mm");
	}

	public SubFolderBuilder formatNumber(String pattern) {
		return format(new DecimalFormat(pattern));
	}

	public String getFolderName(BusinessNode node) {
		Serializable value = node.getRepositoryNode().getProperty(property);
		if (value == null) {
			if (defaultValue == null) {
				throw new IllegalStateException("Value null and no default value given.");
			}
			return defaultValue;
		}
		return (format != null) ? format.format(value) : value.toString();
	}
}
