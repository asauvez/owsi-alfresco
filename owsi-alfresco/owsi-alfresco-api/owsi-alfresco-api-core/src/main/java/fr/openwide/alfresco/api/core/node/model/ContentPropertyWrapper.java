package fr.openwide.alfresco.api.core.node.model;

import java.util.List;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ContentPropertyWrapper {

	private final RepositoryNode node;
	private final NameReference contentProperty;
	private final List<Object> path;

	public ContentPropertyWrapper(RepositoryNode node, NameReference contentProperty, List<Object> path) {
		this.node = node;
		this.contentProperty = contentProperty;
		this.path = path;
	}

	public RepositoryNode getNode() {
		return node;
	}
	public NameReference getContentProperty() {
		return contentProperty;
	}
	public List<Object> getPath() {
		return path;
	}

}
