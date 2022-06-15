package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.alfresco.service.cmr.version.VersionType;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class NewVersionBuilder {
	
	private String description;
	private VersionType versionType;
	private Collection<PropertyModel<?>> propertiesToCopy = new ArrayList<>();
	
	public String getDescription() {
		return description;
	}
	public NewVersionBuilder description(String description) {
		this.description = description;
		return this;
	}
	
	public VersionType getVersionType() {
		return versionType;
	}
	public NewVersionBuilder versionType(VersionType versionType) {
		this.versionType = versionType;
		return this;
	}
	
	public Collection<PropertyModel<?>> getPropertiesToCopy() {
		return propertiesToCopy;
	}
	public NewVersionBuilder propertiesToCopy(PropertyModel<?> ... propertiesToCopy) {
		this.propertiesToCopy.addAll(Arrays.asList(propertiesToCopy));
		return this;
	}
}
