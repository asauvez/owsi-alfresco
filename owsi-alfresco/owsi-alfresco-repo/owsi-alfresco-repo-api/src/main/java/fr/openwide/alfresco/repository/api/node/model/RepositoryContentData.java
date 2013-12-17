package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.Locale;

public class RepositoryContentData implements Serializable {

	private String mimetype;
	private String mimetypeDisplay;
	private long size;
	private String encoding;
	private Locale locale;

	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}

	public String getMimetypeDisplay() {
		return mimetypeDisplay;
	}
	public void setMimetypeDisplay(String mimetypeDisplay) {
		this.mimetypeDisplay = mimetypeDisplay;
	}

	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}

	public String getEncoding() {
		return encoding;
	}
	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}
}
