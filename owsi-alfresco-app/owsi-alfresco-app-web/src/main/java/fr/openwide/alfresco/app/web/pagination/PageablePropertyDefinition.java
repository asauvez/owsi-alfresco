package fr.openwide.alfresco.app.web.pagination;

public interface PageablePropertyDefinition<T, U extends Comparable<U>> {

	U getPropertyValue(T object);

}
