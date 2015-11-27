package fr.openwide.alfresco.app.web.download.model;

import javax.servlet.http.HttpServletResponse;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class NodeReferenceDownloadResponse extends DownloadResponse {

	private static final NameReference DEFAULT_CONTENT_PROPERTY = NameReference.create("cm", "content");
	
	private NodeReference nodeReference;
	private NameReference property = DEFAULT_CONTENT_PROPERTY;
	private RenditionDownloadResponse rendition = new RenditionDownloadResponse(this);

	public NodeReferenceDownloadResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}
	
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

	public RenditionDownloadResponse rendition() {
		return rendition;
	}
}
