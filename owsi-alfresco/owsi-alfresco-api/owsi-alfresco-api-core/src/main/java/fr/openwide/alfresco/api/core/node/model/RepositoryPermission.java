package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class RepositoryPermission implements Serializable {

	private static final long serialVersionUID = -783188298176667409L;

	public static final RepositoryPermission ADD_CHILDREN = new RepositoryPermission("AddChildren");
	public static final RepositoryPermission DELETE = new RepositoryPermission("Delete"); // Delete children, link

	public static final RepositoryPermission READ = new RepositoryPermission("Read"); // Read properties, content, children
	public static final RepositoryPermission READ_PROPERTIES = new RepositoryPermission("ReadProperties");
	public static final RepositoryPermission READ_CONTENT = new RepositoryPermission("ReadContent");

	public static final RepositoryPermission WRITE = new RepositoryPermission("Write"); // Write properties, content
	public static final RepositoryPermission WRITE_PROPERTIES = new RepositoryPermission("WriteProperties");
	public static final RepositoryPermission WRITE_CONTENT = new RepositoryPermission("WriteContent");

	public static final RepositoryPermission COORDINATOR = new RepositoryPermission("Coordinator");
	public static final RepositoryPermission COLLABORATOR = new RepositoryPermission("Collaborator");
	public static final RepositoryPermission CONTRIBUTOR = new RepositoryPermission("Contributor");
	public static final RepositoryPermission EDITOR = new RepositoryPermission("Editor");
	public static final RepositoryPermission CONSUMER = new RepositoryPermission("Consumer");

	private String name;

	public RepositoryPermission(String name) {
		this.name = name;
	}

	@JsonValue
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object object) {
		if (object == null) {
			return false;
		}
		if (object == this) {
			return true;
		}
		if (object instanceof RepositoryPermission) {
			RepositoryPermission other = (RepositoryPermission) object;
			return Objects.equals(name, other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
