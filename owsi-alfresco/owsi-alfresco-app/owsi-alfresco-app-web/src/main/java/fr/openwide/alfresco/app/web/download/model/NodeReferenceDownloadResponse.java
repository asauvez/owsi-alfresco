package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeReferenceDownloadResponse extends DownloadResponse {

	private NodeReference nodeReference;
	private NameReference property;

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

}
