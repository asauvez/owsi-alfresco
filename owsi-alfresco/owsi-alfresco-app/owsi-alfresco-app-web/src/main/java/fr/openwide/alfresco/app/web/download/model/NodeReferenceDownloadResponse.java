package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class NodeReferenceDownloadResponse extends DownloadResponse {

	private static final String THUMBNAIL_WEB_PREVIEW = "webpreview";
	
	private NodeReference nodeReference;
	private NameReference property;
	private String thumbnailName;

	public NodeReference getNodeReference() {
		return nodeReference;
	}
	public NodeReferenceDownloadResponse nodeReference(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
		return this;
	}

	public NameReference getProperty() {
		return property;
	}
	public NodeReferenceDownloadResponse property(NameReference property) {
		this.property = property;
		return this;
	}

	public String getThumbnailName() {
		return thumbnailName;
	}
	public NodeReferenceDownloadResponse thumbnailName(String thumbnailName) {
		this.thumbnailName = thumbnailName;
		return this;
	}
	public NodeReferenceDownloadResponse thumbnailWebPreview() {
		return thumbnailName(THUMBNAIL_WEB_PREVIEW);
	}
}
