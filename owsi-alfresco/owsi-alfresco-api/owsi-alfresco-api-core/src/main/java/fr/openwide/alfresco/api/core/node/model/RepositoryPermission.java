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

	public static final RepositoryPermission READ_PERMISSIONS = new RepositoryPermission("ReadPermissions");
	public static final RepositoryPermission CHANGE_PERMISSIONS = new RepositoryPermission("ChangePermissions");

	public static final RepositoryPermission CREATE_ASSOCIATIONS = new RepositoryPermission("CreateAssociations");
	public static final RepositoryPermission READ_ASSOCIATIONS = new RepositoryPermission("ReadAssociations");
	public static final RepositoryPermission DELETE_ASSOCIATIONS = new RepositoryPermission("DeleteAssociations");

	public static final RepositoryPermission TAKE_OWNERSHIP = new RepositoryPermission("TakeOwnership");
	public static final RepositoryPermission SET_OWNER = new RepositoryPermission("SetOwner");
	public static final RepositoryPermission LOCK = new RepositoryPermission("Lock");
	public static final RepositoryPermission UNLOCK = new RepositoryPermission("Unlock");
	public static final RepositoryPermission CHECK_OUT = new RepositoryPermission("CheckOut");
	public static final RepositoryPermission CHECK_IN = new RepositoryPermission("CheckIn");
	public static final RepositoryPermission CANCEL_CHECK_OUT = new RepositoryPermission("CancelCheckOut");

	public static final RepositoryPermission COORDINATOR = new RepositoryPermission("Coordinator");
	public static final RepositoryPermission COLLABORATOR = new RepositoryPermission("Collaborator");
	public static final RepositoryPermission CONTRIBUTOR = new RepositoryPermission("Contributor");
	public static final RepositoryPermission EDITOR = new RepositoryPermission("Editor");
	public static final RepositoryPermission CONSUMER = new RepositoryPermission("Consumer");

	public static final RepositoryPermission SITE_MANAGER = new RepositoryPermission("SiteManager");
	public static final RepositoryPermission SITE_COLLABORATOR = new RepositoryPermission("SiteCollaborator");
	public static final RepositoryPermission SITE_CONTRIBUTOR = new RepositoryPermission("SiteContributor");
	public static final RepositoryPermission SITE_CONSUMER = new RepositoryPermission("SiteConsumer");

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
