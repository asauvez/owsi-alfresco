package fr.openwide.alfresco.app.web.download.model;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileDownloadResponse extends ContentDownloadResponse {

	private File file;
	private OutputStream outputStream;

	private InputStream inputStream;

	public FileDownloadResponse() {}

	public FileDownloadResponse(File file, OutputStream outputStream) {
		this.file = file;
		this.outputStream = outputStream;
	}

	@Override
	public OutputStream getOutputStream() {
		return outputStream;
	}

	@Override
	public void flush() throws IOException {
		if (outputStream != null) {
			outputStream.close();
		}
		inputStream = new BufferedInputStream(new FileInputStream(file));
	}

	@Override
	public long getContentLength() {
		return file.length();
	}

	@Override
	public InputStream getWrittableStream() {
		return inputStream;
	}

	@Override
	public void close() throws IOException {
		if (inputStream != null) {
			inputStream.close();
		}
		if (! file.delete()) {
			throw new IOException("Could not delete file: " + file);
		}
	}

}
