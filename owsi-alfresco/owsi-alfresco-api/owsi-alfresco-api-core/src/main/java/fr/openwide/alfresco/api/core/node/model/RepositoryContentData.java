package fr.openwide.alfresco.api.core.node.model;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.util.Locale;

import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

@JsonInclude(Include.NON_EMPTY)
public class RepositoryContentData implements Serializable {

	private static final long serialVersionUID = 7234820018007664746L;

	private String mimetype;
	private String mimetypeDisplay;
	private Long size;
	private String encoding;
	private Locale locale;

	public RepositoryContentData() {}

	public RepositoryContentData(String mimetype) {
		this.mimetype = mimetype;
	}

	public RepositoryContentData(String mimetype, String encoding) {
		this(mimetype);
		this.encoding = encoding;
	}
	public RepositoryContentData(MediaType mimetype, Charset encoding) {
		setMimetype(mimetype);
		setEncoding(encoding);
	}

	public RepositoryContentData(String mimetype, String mimetypeDisplay, Long size, String encoding, Locale locale) {
		this(mimetype, encoding);
		this.mimetypeDisplay = mimetypeDisplay;
		this.size = size;
		this.locale = locale;
	}

	public String getMimetype() {
		return mimetype;
	}
	public void setMimetype(String mimetype) {
		this.mimetype = mimetype;
	}
	public void setMimetype(MediaType mediaType) {
		this.mimetype = mediaType.toString();
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
	public void setEncoding(Charset charset) {
		this.encoding = charset.name();
	}

	public Locale getLocale() {
		return locale;
	}
	public void setLocale(Locale locale) {
		this.locale = locale;
	}

}
