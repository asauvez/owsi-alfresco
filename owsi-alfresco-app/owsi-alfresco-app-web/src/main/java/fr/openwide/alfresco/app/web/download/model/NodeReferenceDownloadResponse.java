package fr.openwide.alfresco.app.web.download.model;

import javax.servlet.http.HttpServletResponse;

import fr.openwide.alfresco.api.core.node.model.RemoteCallParameters;
import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.app.web.download.binding.DownloadResponseHandler;
import fr.openwide.alfresco.component.model.node.model.property.single.ContentPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.CmModel;

public class NodeReferenceDownloadResponse extends DownloadResponse {

	private static final NameReference DEFAULT_CONTENT_PROPERTY = CmModel.content.content.getNameReference();
	
	private NodeReference nodeReference;
	private NameReference property = DEFAULT_CONTENT_PROPERTY;
	private RenditionDownloadResponse rendition = new RenditionDownloadResponse(this);
	private RemoteCallParameters remoteCallParameters = new RemoteCallParameters(); 
	private DownloadResponseHandler alternateHandler;
	
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
	public NodeReferenceDownloadResponse property(ContentPropertyModel property) {
		return property(property.getNameReference());
	}

	public RenditionDownloadResponse rendition() {
		return rendition;
	}
	public RemoteCallParameters getRemoteCallParameters() {
		return remoteCallParameters;
	}
	
	public DownloadResponseHandler getAlternateHandler() {
		return alternateHandler;
	}
	public NodeReferenceDownloadResponse alternateHandler(DownloadResponseHandler alternateHandler) {
		this.alternateHandler = alternateHandler;
		return this;
	}
}
