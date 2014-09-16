package fr.openwide.alfresco.component.model.node.model.constraint;


public class FileNamePropertyConstraint extends RegexPropertyConstraint {

	public static final FileNamePropertyConstraint INSTANCE = new FileNamePropertyConstraint();
	
	protected FileNamePropertyConstraint() {
		super("(.*[\\\"\\*\\\\\\>\\<\\?\\/\\:\\|]+.*)|(.*[\\.]?.*[\\.]+$)|(.*[ ]+$)", false);
	}
}
