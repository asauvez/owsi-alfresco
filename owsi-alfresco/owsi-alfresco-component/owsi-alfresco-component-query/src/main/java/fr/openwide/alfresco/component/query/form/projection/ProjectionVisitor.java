package fr.openwide.alfresco.component.query.form.projection;

public abstract class ProjectionVisitor {

	public void visit(Object o) {
		visitObject(o);
		if (o instanceof ProjectionVisitorAcceptor) {
			((ProjectionVisitorAcceptor) o).accept(this);
		}
	}

	public abstract void visitObject(Object o);
}
