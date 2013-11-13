package fr.openwide.alfresco.query.web.form.projection.button;


public interface TopButtonBuilder<P, T> extends ButtonBuilder<P, T> {

	TopButtonBuilder<P, T> dropDownSeparator();

	ButtonBuilder<TopButtonBuilder<P, T>, T> dropDownButton(String message, Object ... messageArgs);

}
