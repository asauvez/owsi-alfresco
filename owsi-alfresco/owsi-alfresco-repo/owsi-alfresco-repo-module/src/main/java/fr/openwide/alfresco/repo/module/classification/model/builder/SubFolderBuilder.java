package fr.openwide.alfresco.repo.module.classification.model.builder;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class SubFolderBuilder {
	
	private final List<NameReference> properties;
	private Function<Serializable, Serializable> format = null;
	private String defaultValue = null;

	public SubFolderBuilder(NameReference ... properties) {
		this.properties = Arrays.asList(properties);
	}
	public SubFolderBuilder(PropertyModel<?> ... properties) {
		this.properties = Arrays.asList(properties).stream()
				.map(p -> p.getNameReference())
				.collect(Collectors.toList());
	}

	public SubFolderBuilder defaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	public SubFolderBuilder format(Function<Serializable, Serializable> format) {
		if (this.format != null) {
			throw new IllegalStateException("A format is already set.");
		}
		this.format = format;
		return this;
	}
	public SubFolderBuilder format(Format format) {
		return format(o -> format.format(o));
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

	public SubFolderBuilder substringBefore(String separator) {
		return format(s -> StringUtils.substringBefore(s.toString(), separator));
	}

	public SubFolderBuilder regex(String regex, int group) {
		return format(s -> {
			Matcher matcher = Pattern.compile(regex).matcher(s.toString());
			HashSet<String> set = new HashSet<>(); 
			while (matcher.matches()) {
				set.add(matcher.group(group));
			}
			return set;
		});
	}
	
	@SuppressWarnings("unchecked")
	public Set<String> getFoldersName(ClassificationWithRootBuilder builder) {
		return properties.stream()
			.flatMap(property -> {
				Serializable value = builder.getProperty(property);
				Collection<Serializable> result;
				if (value == null) {
					if (defaultValue == null) {
						// throw new IllegalStateException("Value null and no default value given.");
						result = Collections.emptySet();
					} else {
						result = Collections.singletonList(defaultValue);
					}
				} else {
					result = (value instanceof Collection) ? (Collection<Serializable>) value : Collections.singleton(value);
				}
				return result.stream();
			})
			.flatMap(value -> {
				if (format != null) {
					value = format.apply(value);
				}
				return ((value instanceof Collection) ? (Collection<Serializable>) value : Collections.singleton(value)).stream();
			})
			.map(value -> value.toString())
			.collect(Collectors.toSet());
	}
}
