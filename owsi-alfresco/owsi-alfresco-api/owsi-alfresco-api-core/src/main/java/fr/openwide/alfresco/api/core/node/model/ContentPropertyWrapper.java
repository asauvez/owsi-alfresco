package fr.openwide.alfresco.api.core.node.model;

import java.util.List;

import fr.openwide.alfresco.api.core.node.binding.content.ZipIterator;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public class ContentPropertyWrapper {

	private final RepositoryNode node;
	private final NameReference contentProperty;
	private final List<Object> path;
	
	private final int contentId;
	private final ZipIterator zipIterator;

	public ContentPropertyWrapper(RepositoryNode node, NameReference contentProperty, List<Object> path, 
			int contentId, ZipIterator zipIterator) {
		this.node = node;
		this.contentProperty = contentProperty;
		this.path = path;
		
		this.contentId = contentId;
		this.zipIterator = zipIterator;
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

	public int getContentId() {
		return contentId;
	}
	public ZipIterator getZipIterator() {
		return zipIterator;
	}
}
