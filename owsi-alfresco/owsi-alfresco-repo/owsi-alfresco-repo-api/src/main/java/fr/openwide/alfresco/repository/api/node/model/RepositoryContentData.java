package fr.openwide.alfresco.repository.api.node.model;

import java.io.Serializable;
import java.util.Locale;

public class RepositoryContentData implements Serializable {

	private String mimetype;
	private String mimetypeDisplay;
	private Long size;
	private String encoding;
	private Locale locale;

	public RepositoryContentData() {
	}

	public RepositoryContentData(String mimetype) {
		this.mimetype = mimetype;
	}

	public RepositoryContentData(String mimetype, String encoding) {
		this.mimetype = mimetype;
		this.encoding = encoding;
	}

	public RepositoryContentData(String mimetype, String mimetypeDisplay, Long size, String encoding, Locale locale) {
		this.mimetype = mimetype;
		this.mimetypeDisplay = mimetypeDisplay;
		this.size = size;
		this.encoding = encoding;
		this.locale = locale;
	}

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

	public Long getSize() {
		return size;
	}
	public void setSize(Long size) {
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
