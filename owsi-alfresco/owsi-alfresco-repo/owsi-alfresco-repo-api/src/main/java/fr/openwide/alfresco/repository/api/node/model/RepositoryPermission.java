package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class RepositoryPermission implements Serializable {
	
	public static final RepositoryPermission ADD_CHILDREN = new RepositoryPermission("AddChildren");

	public static final RepositoryPermission READ_PROPERTIES = new RepositoryPermission("ReadProperties");
	public static final RepositoryPermission WRITE_PROPERTIES = new RepositoryPermission("WriteProperties");

	public static final RepositoryPermission DELETE_NODE = new RepositoryPermission("DeleteNode");

	public static final RepositoryPermission DELETE_CHILDREN = new RepositoryPermission("DeleteChildren");
	public static final RepositoryPermission CREATE_CHILDREN = new RepositoryPermission("CreateChildren");

	public static final RepositoryPermission READ_CONTENT = new RepositoryPermission("ReadContent");
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
			return Objects.equals(getName(), other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(getName());
	}}
