package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;


public abstract class DownloadResponse {

	private String contentName;
	private AlertContainer alertContainer = new AlertContainer();
	private boolean attachement = true;
	
	public String getContentName() {
		return contentName;
	}
	public void setContentName(String contentName) {
		this.contentName = contentName;
	}

	public AlertContainer getAlertContainer() {
		return alertContainer;
	}
	public void setAlertContainer(AlertContainer alertContainer) {
		this.alertContainer = alertContainer;
	}

	public boolean isAttachement() {
		return attachement;
	}
	public void setAttachement(boolean attachement) {
		this.attachement = attachement;
	}
}
