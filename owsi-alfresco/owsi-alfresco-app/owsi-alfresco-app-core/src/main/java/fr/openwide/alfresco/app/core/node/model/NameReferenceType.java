package fr.openwide.alfresco.app.core.node.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class NameReferenceType extends AbstractStringUserType<NameReference> {

	@Override
	public Class<NameReference> returnedClass() {
		return NameReference.class;
	}

	@Override
	protected NameReference getAsObject(String value) {
		return NameReference.create(value);
	}
	
}
