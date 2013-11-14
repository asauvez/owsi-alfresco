package fr.openwide.alfresco.query.web.form.projection.button;

import com.google.common.base.Predicate;

public interface ButtonBuilder<PARENT, I> {

	PARENT of();

	ButtonBuilder<PARENT, I> visible(Predicate<? super I> visible);	
	ButtonBuilder<PARENT, I> visible(boolean visible);

	ButtonBuilder<PARENT, I> enabled(Predicate<? super I> enabled);
	ButtonBuilder<PARENT, I> enabled(boolean enabled);

	ButtonBuilder<PARENT, I> primary();
	ButtonBuilder<PARENT, I> primary(boolean primary);

	ButtonBuilder<PARENT, I> icon(String iconClass);
	ButtonBuilder<PARENT, I> cssClass(String cssClass);

	ButtonBuilder<PARENT, I> url(String urlPattern);

}
