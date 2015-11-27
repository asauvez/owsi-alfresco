package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.app.core.node.model.RenditionsSetter;


public class RenditionDownloadResponse extends RenditionsSetter<NodeReferenceDownloadResponse> {

	private NodeReferenceDownloadResponse downloadResponse;
	private NameReference name;

	public RenditionDownloadResponse(NodeReferenceDownloadResponse downloadResponse) {
		this.downloadResponse = downloadResponse;
	}

	@Override
	public NodeReferenceDownloadResponse name(NameReference renditionName) {
		this.name = renditionName;
		return downloadResponse;
	}

	public NameReference getName() {
		return name;
	}
}
