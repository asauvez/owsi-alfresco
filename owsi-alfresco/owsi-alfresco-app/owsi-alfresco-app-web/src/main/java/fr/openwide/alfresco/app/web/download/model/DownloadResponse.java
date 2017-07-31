package fr.openwide.alfresco.app.web.download.model;

import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;

import fr.openwide.alfresco.app.web.validation.model.AlertContainer;

public abstract class DownloadResponse {

	private boolean attachment = true; // force le téléchargement
	private String attachmentName;

	private AlertContainer alertContainer = new AlertContainer();
	private HttpServletResponse httpServletResponse;

	// Ajouter à la fin du téléchargement. 
	// Permet d'identifier l'utilisateur ayant téléchargé un document par exemple.
	private byte[] contentWatermark = null;
	
	private Long contentRangeStart = null;
	private Long contentRangeEnd = null;
	
	public DownloadResponse(HttpServletResponse httpServletResponse) {
		this.httpServletResponse = httpServletResponse;
	}

	public HttpServletResponse getServletResponse() {
		return httpServletResponse;
	}
	public DownloadResponse noCache() {
		httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "no-cache");
		httpServletResponse.setHeader(HttpHeaders.PRAGMA, "no-cache");
		return this;
	} 
	public DownloadResponse cache(int duration, TimeUnit timeUnit) {
		httpServletResponse.setHeader(HttpHeaders.CACHE_CONTROL, "max-age=" + timeUnit.toSeconds(duration));
		return this;
	} 
	
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
	
	public byte[] getContentWatermark() {
		return contentWatermark;
	}
	public DownloadResponse contentWatermark(byte[] contentWatermark) {
		this.contentWatermark = contentWatermark;
		return this;
	}

	
	public boolean hasContentRange() {
		return contentRangeStart != null || contentRangeEnd != null;
	}
	public Long getContentRangeStart() {
		return contentRangeStart;
	}
	public DownloadResponse contentRangeStart(Long contentRangeStart) {
		this.contentRangeStart = contentRangeStart;
		return this;
	}
	public Long getContentRangeEnd() {
		return contentRangeEnd;
	}
	public DownloadResponse contentRangeEnd(Long contentRangeEnd) {
		this.contentRangeEnd = contentRangeEnd;
		return this;
	}
	
	private static final String BYTES_PREFIX = "bytes=";
	public DownloadResponse contentRange(String contentRange) {
		if (contentRange != null) {
			if (! contentRange.startsWith(BYTES_PREFIX)) {
				throw new IllegalStateException("Range shoud begin with byte, but start with " + contentRange);
			}
			contentRange = contentRange.substring(BYTES_PREFIX.length());
			int pos = contentRange.indexOf("-");
			if (pos != 0) {
				contentRangeStart = Long.parseLong(contentRange.substring(0,  pos).trim());
			}
			if (contentRange.length() > pos+1) {
				contentRangeEnd = Long.parseLong(contentRange.substring(pos+1).trim());
			}
		}
		return this;
	}
	public long getContentLength(long realContentLength) {
		if (! hasContentRange()) {
			return realContentLength;
		} else {
			return ((contentRangeEnd != null) ? contentRangeEnd : realContentLength) 
				 - ((contentRangeStart != null) ? contentRangeStart : 0L) 
				 + 1L;
		}
	}
	public String getContentRange(long realContentLength) {
		if (! hasContentRange()) {
			return null;
		}
		return BYTES_PREFIX 
				+ ((contentRangeStart != null) ? Long.toString(contentRangeStart) : "")
				+ "-"
				+ ((contentRangeEnd != null) ? Long.toString(contentRangeEnd) : "")
				+ "/" + realContentLength;
	}
}
