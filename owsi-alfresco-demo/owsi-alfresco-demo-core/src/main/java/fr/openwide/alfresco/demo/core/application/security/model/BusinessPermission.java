package fr.openwide.alfresco.demo.core.application.security.model;

import fr.openwide.core.jpa.security.model.NamedPermission;

public class BusinessPermission extends NamedPermission {

	private static final long serialVersionUID = -7674572786179884167L;

	protected BusinessPermission(String name) {
		super(name);
	}
	
}
