package fr.openwide.alfresco.query.web.form.projection.button;

import com.google.common.base.Predicate;

import fr.openwide.alfresco.query.core.search.model.NodeResult;

public interface ButtonBuilder<P, T> {

	P of();

	ButtonBuilder<P, T> visible(Predicate<? super NodeResult> visible);	
	ButtonBuilder<P, T> visible(boolean visible);

	ButtonBuilder<P, T> enabled(Predicate<? super NodeResult> enabled);
	ButtonBuilder<P, T> enabled(boolean enabled);

	ButtonBuilder<P, T> primary();
	ButtonBuilder<P, T> primary(boolean primary);

	ButtonBuilder<P, T> icon(String iconClass);
	ButtonBuilder<P, T> cssClass(String cssClass);

	ButtonBuilder<P, T> url(String urlPattern);

}
