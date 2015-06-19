package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class NodeReferenceDownloadResponse extends DownloadResponse {

	private static final NameReference DEFAULT_CONTENT_PROPERTY = NameReference.create("cm", "content");
	
	private NodeReference nodeReference;
	private NameReference property = DEFAULT_CONTENT_PROPERTY;
	private NameReference renditionName;

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

	public NameReference getRenditionName() {
		return renditionName;
	}
	public NodeReferenceDownloadResponse renditionName(String renditionName) {
		return renditionName(NameReference.create("cm", renditionName));
	}
	public NodeReferenceDownloadResponse renditionName(NameReference renditionName) {
		this.renditionName = renditionName;
		return this;
	}
	public NodeReferenceDownloadResponse renditionImgPreview() {
		return renditionName("imgpreview");
	}
}
