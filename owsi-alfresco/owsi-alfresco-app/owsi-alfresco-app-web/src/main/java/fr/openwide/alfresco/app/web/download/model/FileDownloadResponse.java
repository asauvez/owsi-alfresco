package fr.openwide.alfresco.app.web.download.model;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import fr.openwide.alfresco.api.core.util.ThresholdBuffer;

public class FileDownloadResponse extends ContentDownloadResponse {

	private ThresholdBuffer thresholdBuffer;
	
	public FileDownloadResponse(HttpServletResponse httpServletResponse, ThresholdBuffer thresholdBuffer) {
		super(httpServletResponse);
		
		this.thresholdBuffer = thresholdBuffer;
	}

	@Override
	public OutputStream getOutputStream() {
		return thresholdBuffer;
	}

	@Override
	public void flush() throws IOException {
		thresholdBuffer.flush();
	}

	@Override
	public long getContentLength() {
		return thresholdBuffer.getSize();
	}

	@Override
	public InputStream getWrittableStream() {
		return thresholdBuffer.newInputStream();
	}

	@Override
	public void close() throws IOException {
		thresholdBuffer.close();
	}
}
