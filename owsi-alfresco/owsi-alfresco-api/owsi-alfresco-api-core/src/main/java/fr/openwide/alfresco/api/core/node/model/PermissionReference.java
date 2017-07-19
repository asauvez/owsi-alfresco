package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

public class PermissionReference implements Serializable {

	private static final long serialVersionUID = -783188298176667409L;

	public static final PermissionReference ADD_CHILDREN = new PermissionReference("AddChildren");
	public static final PermissionReference DELETE = new PermissionReference("Delete"); // Delete children, link

	public static final PermissionReference READ = new PermissionReference("Read"); // Read properties, content, children
	public static final PermissionReference READ_PROPERTIES = new PermissionReference("ReadProperties");
	public static final PermissionReference READ_CONTENT = new PermissionReference("ReadContent");

	public static final PermissionReference WRITE = new PermissionReference("Write"); // Write properties, content
	public static final PermissionReference WRITE_PROPERTIES = new PermissionReference("WriteProperties");
	public static final PermissionReference WRITE_CONTENT = new PermissionReference("WriteContent");

	public static final PermissionReference READ_PERMISSIONS = new PermissionReference("ReadPermissions");
	public static final PermissionReference CHANGE_PERMISSIONS = new PermissionReference("ChangePermissions");

	public static final PermissionReference CREATE_ASSOCIATIONS = new PermissionReference("CreateAssociations");
	public static final PermissionReference READ_ASSOCIATIONS = new PermissionReference("ReadAssociations");
	public static final PermissionReference DELETE_ASSOCIATIONS = new PermissionReference("DeleteAssociations");

	public static final PermissionReference TAKE_OWNERSHIP = new PermissionReference("TakeOwnership");
	public static final PermissionReference SET_OWNER = new PermissionReference("SetOwner");
	public static final PermissionReference LOCK = new PermissionReference("Lock");
	public static final PermissionReference UNLOCK = new PermissionReference("Unlock");
	public static final PermissionReference CHECK_OUT = new PermissionReference("CheckOut");
	public static final PermissionReference CHECK_IN = new PermissionReference("CheckIn");
	public static final PermissionReference CANCEL_CHECK_OUT = new PermissionReference("CancelCheckOut");

	public static final PermissionReference COORDINATOR = new PermissionReference("Coordinator");
	public static final PermissionReference COLLABORATOR = new PermissionReference("Collaborator");
	public static final PermissionReference CONTRIBUTOR = new PermissionReference("Contributor");
	public static final PermissionReference EDITOR = new PermissionReference("Editor");
	public static final PermissionReference CONSUMER = new PermissionReference("Consumer");

	public static final PermissionReference SITE_MANAGER = new PermissionReference("SiteManager");
	public static final PermissionReference SITE_COLLABORATOR = new PermissionReference("SiteCollaborator");
	public static final PermissionReference SITE_CONTRIBUTOR = new PermissionReference("SiteContributor");
	public static final PermissionReference SITE_CONSUMER = new PermissionReference("SiteConsumer");

	private String name;

	public PermissionReference(String name) {
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
		if (object instanceof PermissionReference) {
			PermissionReference other = (PermissionReference) object;
			return Objects.equals(name, other.getName());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(name);
	}

}
