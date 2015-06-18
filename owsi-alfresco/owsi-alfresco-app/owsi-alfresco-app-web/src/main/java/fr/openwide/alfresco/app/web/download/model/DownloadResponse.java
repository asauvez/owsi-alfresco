package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;

public abstract class DownloadResponse {

	private boolean attachment = true; // force le téléchargement
	private String attachmentName;
	private boolean noCache = false;

	private AlertContainer alertContainer = new AlertContainer();

	public AlertContainer getAlertContainer() {
		return alertContainer;
	}
	public DownloadResponse alertContainer(AlertContainer alertContainer) {
		this.alertContainer = alertContainer;
		return this;
	}

	public boolean isAttachment() {
		return attachment;
	}
	public DownloadResponse attachment(boolean attachment) {
		this.attachment = attachment;
		return this;
	}
	
	public String getAttachmentName() {
		return attachmentName;
	}
	public DownloadResponse attachmentName(String attachmentName) {
		this.attachmentName = attachmentName;
		return this;
	}

	public boolean isNoCache() {
		return noCache;
	}
	public DownloadResponse noCache(boolean noCache) {
		this.noCache = noCache;
		return this;
	}

}
