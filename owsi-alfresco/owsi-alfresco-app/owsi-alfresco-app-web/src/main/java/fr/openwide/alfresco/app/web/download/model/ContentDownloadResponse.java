package fr.openwide.alfresco.app.web.download.model;

import java.io.Closeable;
import java.io.Flushable;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class ContentDownloadResponse extends DownloadResponse  implements Flushable, Closeable {

	private String contentType;

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
