package fr.openwide.alfresco.app.web.download.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.output.CountingOutputStream;

public class ByteArrayDownloadResponse extends ContentDownloadResponse {

	private ByteArrayOutputStream baos;
	private CountingOutputStream outputStream;

	public ByteArrayDownloadResponse() {}

	public ByteArrayDownloadResponse(ByteArrayOutputStream baos) {
		this.baos = baos;
		this.outputStream = new CountingOutputStream(baos);
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void flush() throws IOException {
		// nothing to do
	}

	@Override
	public long getContentLength() {
		return outputStream.getByteCount();
	}

	@Override
	public InputStream getWrittableStream() {
		return new ByteArrayInputStream(baos.toByteArray());
	}

	@Override
	public void close() throws IOException {
		// nothing to do
	}

}
