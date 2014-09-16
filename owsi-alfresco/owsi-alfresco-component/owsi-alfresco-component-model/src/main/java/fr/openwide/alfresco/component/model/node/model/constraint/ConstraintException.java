package fr.openwide.alfresco.component.model.node.model.constraint;


public class ConstraintException extends RuntimeException {

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
