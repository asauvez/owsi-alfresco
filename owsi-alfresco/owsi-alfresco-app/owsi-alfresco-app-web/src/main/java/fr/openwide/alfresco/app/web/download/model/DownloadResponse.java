package fr.openwide.alfresco.app.web.download.model;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;


public abstract class DownloadResponse {

	private boolean attachement = true;
	private String attachementName;
	
	private AlertContainer alertContainer = new AlertContainer();
	
	public AlertContainer getAlertContainer() {
		return alertContainer;
	}
	public DownloadResponse alertContainer(AlertContainer alertContainer) {
		this.alertContainer = alertContainer;
		return this;
	}

	public boolean isAttachement() {
		return attachement;
	}
	/** Si vrai (le défaut), force le téléchargement 
	 * @return */
	public DownloadResponse attachement(boolean attachement) {
		this.attachement = attachement;
		return this;
	}

	public String getAttachementName() {
		return attachementName;
	}
	public DownloadResponse attachementName(String attachementName) {
		this.attachementName = attachementName;
		return this;
	}
}
