package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonValue;

/** http://docs.alfresco.com/6.0/references/permissions_share_components.html */
public class PermissionReference implements Serializable {

	 /** Lecture seule */
	public static final PermissionReference CONSUMER = PermissionReference.create("Consumer");
	/** Rajoute doc et édite ses propres doc */
	public static final PermissionReference CONTRIBUTOR = PermissionReference.create("Contributor");
	/** Edites documents des autres */
	public static final PermissionReference COLLABORATOR = PermissionReference.create("Collaborator");
	public static final PermissionReference EDITOR = PermissionReference.create("Editor");
	/** Gére permissions */
	public static final PermissionReference COORDINATOR = PermissionReference.create("Coordinator"); // 
	
	public static final PermissionReference SITE_MANAGER = PermissionReference.create("SiteManager");
	public static final PermissionReference SITE_COLLABORATOR = PermissionReference.create("SiteCollaborator");
	public static final PermissionReference SITE_CONTRIBUTOR = PermissionReference.create("SiteContributor");
	public static final PermissionReference SITE_CONSUMER = PermissionReference.create("SiteConsumer");

	
	
	public static final PermissionReference ADD_CHILDREN = PermissionReference.create("AddChildren");
	public static final PermissionReference DELETE = PermissionReference.create("Delete"); // Delete children, link

	public static final PermissionReference READ = PermissionReference.create("Read"); // Read properties, content, children
	public static final PermissionReference READ_PROPERTIES = PermissionReference.create("ReadProperties");
	public static final PermissionReference READ_CONTENT = PermissionReference.create("ReadContent");

	public static final PermissionReference WRITE = PermissionReference.create("Write"); // Write properties, content
	public static final PermissionReference WRITE_PROPERTIES = PermissionReference.create("WriteProperties");
	public static final PermissionReference WRITE_CONTENT = PermissionReference.create("WriteContent");

	public static final PermissionReference READ_PERMISSIONS = PermissionReference.create("ReadPermissions");
	public static final PermissionReference CHANGE_PERMISSIONS = PermissionReference.create("ChangePermissions");

	public static final PermissionReference CREATE_ASSOCIATIONS = PermissionReference.create("CreateAssociations");
	public static final PermissionReference READ_ASSOCIATIONS = PermissionReference.create("ReadAssociations");
	public static final PermissionReference DELETE_ASSOCIATIONS = PermissionReference.create("DeleteAssociations");

	public static final PermissionReference TAKE_OWNERSHIP = PermissionReference.create("TakeOwnership");
	public static final PermissionReference SET_OWNER = PermissionReference.create("SetOwner");
	public static final PermissionReference LOCK = PermissionReference.create("Lock");
	public static final PermissionReference UNLOCK = PermissionReference.create("Unlock");
	public static final PermissionReference CHECK_OUT = PermissionReference.create("CheckOut");
	public static final PermissionReference CHECK_IN = PermissionReference.create("CheckIn");
	public static final PermissionReference CANCEL_CHECK_OUT = PermissionReference.create("CancelCheckOut");

	private String name;

	private PermissionReference(String name) {
		this.name = name;
	}

	public static PermissionReference create(String name) {
		return new PermissionReference(name);
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
