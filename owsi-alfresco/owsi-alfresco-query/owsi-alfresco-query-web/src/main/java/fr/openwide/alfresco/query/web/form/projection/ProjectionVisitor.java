package fr.openwide.alfresco.query.web.form.projection;

public abstract class ProjectionVisitor {

	public void visit(Object o) {
		visitObject(o);
		if (o instanceof ProjectionVisitorAcceptor) {
			((ProjectionVisitorAcceptor) o).accept(this);
		}
	}

	public abstract void visitObject(Object o);
}
