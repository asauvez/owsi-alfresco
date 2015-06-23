package fr.openwide.alfresco.app.web.download.model;

import java.io.Flushable;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

public abstract class ContentDownloadResponse extends DownloadResponse implements Flushable, AutoCloseable {

	private String contentType;

	public ContentDownloadResponse(HttpServletResponse httpServletResponse) {
		super(httpServletResponse);
	}
	
	public abstract OutputStream getOutputStream();

	public abstract long getContentLength();

	public abstract InputStream getWrittableStream();

	public String getContentType() {
		return contentType;
	}
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

}
