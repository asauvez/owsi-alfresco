package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;
import fr.openwide.alfresco.repository.api.remote.model.NodeReference;

public class NodeReferenceDownloadResponse extends DownloadResponse {

	private NodeReference nodeReference;
	private NameReference property;

	public NodeReference getNodeReference() {
		return nodeReference;
	}
	public void setNodeReference(NodeReference nodeReference) {
		this.nodeReference = nodeReference;
	}

	public NameReference getProperty() {
		return property;
	}
	public void setProperty(NameReference property) {
		this.property = property;
	}
}
