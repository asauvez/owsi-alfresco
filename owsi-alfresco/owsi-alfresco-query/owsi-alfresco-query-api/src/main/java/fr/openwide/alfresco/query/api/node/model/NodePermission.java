package fr.openwide.alfresco.query.api.node.model;

public enum NodePermission {
	Read,
	Write,
	Delete,
	AddChildren,
	ReadProperties,
	ReadChildren,
	WriteProperties,
	DeleteNode,
	DeleteChildren,
	CreateChildren,
	LinkChildren,
	DeleteAssociations,
	ReadAssociations,
	CreateAssociations,
	ReadPermissions,
	ChangePermissions,
	Execute,
	ReadContent,
	WriteContent,
	ExecuteContent,
	TakeOwnership,
	SetOwner,
	Coordinator,
	Contributor,
	Editor,
	Consumer,
	Lock,
	Unlock,
	CheckOut,
	CheckIn,
	CancelCheckOut
	;
	public String getName() {
		return name();
	}
}
