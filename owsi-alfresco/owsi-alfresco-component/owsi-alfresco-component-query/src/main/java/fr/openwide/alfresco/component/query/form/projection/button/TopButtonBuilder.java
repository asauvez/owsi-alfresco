package fr.openwide.alfresco.component.query.form.projection.button;


public interface TopButtonBuilder<PARENT, I> extends ButtonBuilder<PARENT, I> {

	TopButtonBuilder<PARENT, I> dropDownSeparator();

	ButtonBuilder<TopButtonBuilder<PARENT, I>, I> dropDownButton(String message, Object ... messageArgs);

}
