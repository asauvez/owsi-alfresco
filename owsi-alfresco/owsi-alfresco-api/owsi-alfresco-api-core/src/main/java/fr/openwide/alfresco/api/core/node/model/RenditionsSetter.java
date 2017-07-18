package fr.openwide.alfresco.api.core.node.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public abstract class RenditionsSetter<R> {

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
	// PDF
	private static final NameReference PDF_RENDITION = NameReference.create("cm", "pdf");

	public abstract R name(NameReference renditionName);
	
	public R mediumPreview() {
		return name(MEDIUM_PREVIEW_RENDITION);
	}
	public R docLibPreview() {
		return name(DOCLIB_PREVIEW_RENDITION);
	}
	public R imgPreview() {
		return name(IMG_PREVIEW_RENDITION);
	}
	public R avatarPreview() {
		return name(AVATAR_PREVIEW_RENDITION);
	}
	public R avatar32Preview() {
		return name(AVATAR32_PREVIEW_RENDITION);
	}
	public R webPreview() {
		return name(WEB_PREVIEW_RENDITION);
	}
	public R pdf() {
		return name(PDF_RENDITION);
	}
}
