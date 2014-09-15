package fr.openwide.alfresco.repository.api.node.model;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class ContentPropertyWrapper {

	private final RepositoryNode node;
	private final NameReference contentProperty;

	public ContentPropertyWrapper(RepositoryNode node, NameReference contentProperty) {
		this.node = node;
		this.contentProperty = contentProperty;
	}

	public RepositoryNode getNode() {
		return node;
	}
	public NameReference getContentProperty() {
		return contentProperty;
	}

}
