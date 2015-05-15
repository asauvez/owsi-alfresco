package fr.openwide.alfresco.component.model.node.model.constraint;

public class ConstraintException extends RuntimeException {

	private static final long serialVersionUID = -5592448717290919490L;

	public ConstraintException() {}

	public ConstraintException(String msg) {
		super(msg);
	}

	public ConstraintException(Throwable t) {
		super(t);
	}

	public ConstraintException(String msg, Throwable t) {
		super(msg, t);
	}

}
