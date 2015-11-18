package fr.openwide.alfresco.app.web.download.model;

import javax.servlet.http.HttpServletResponse;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NodeReference;

public class NodeReferenceDownloadResponse extends DownloadResponse {

	// cf. thumbnail-service-context.xml
	// 100x100 
	private static final NameReference MEDIUM_PREVIEW_RENDITION = NameReference.create("cm", "medium");
	// 100x100 sans enlargement
	private static final NameReference DOCLIB_PREVIEW_RENDITION = NameReference.create("cm", "doclib");
	// 960x960
	private static final NameReference IMG_PREVIEW_RENDITION = NameReference.create("cm", "imgpreview");
	// User avatar 64x64 image thumbnail options
	private static final NameReference AVATAR_PREVIEW_RENDITION = NameReference.create("cm", "avatar");
	// User avatar 32x32 image thumbnail options
	private static final NameReference AVATAR32_PREVIEW_RENDITION = NameReference.create("cm", "avatar32");
	// shockwave-flash
	private static final NameReference WEB_PREVIEW_RENDITION = NameReference.create("cm", "webpreview");

	private static final NameReference DEFAULT_CONTENT_PROPERTY = NameReference.create("cm", "content");
	
	private NodeReference nodeReference;
	private NameReference property = DEFAULT_CONTENT_PROPERTY;
	private NameReference renditionName;

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

	public NameReference getRenditionName() {
		return renditionName;
	}
	public NodeReferenceDownloadResponse renditionName(NameReference renditionName) {
		this.renditionName = renditionName;
		return this;
	}
	public NodeReferenceDownloadResponse renditionMediumPreview() {
		return renditionName(MEDIUM_PREVIEW_RENDITION);
	}
	public NodeReferenceDownloadResponse renditionDocLibPreview() {
		return renditionName(DOCLIB_PREVIEW_RENDITION);
	}
	public NodeReferenceDownloadResponse renditionImgPreview() {
		return renditionName(IMG_PREVIEW_RENDITION);
	}
	public NodeReferenceDownloadResponse renditionAvatarPreview() {
		return renditionName(AVATAR_PREVIEW_RENDITION);
	}
	public NodeReferenceDownloadResponse renditionAvatar32Preview() {
		return renditionName(AVATAR32_PREVIEW_RENDITION);
	}
	public NodeReferenceDownloadResponse renditionWebPreview() {
		return renditionName(WEB_PREVIEW_RENDITION);
	}
}
