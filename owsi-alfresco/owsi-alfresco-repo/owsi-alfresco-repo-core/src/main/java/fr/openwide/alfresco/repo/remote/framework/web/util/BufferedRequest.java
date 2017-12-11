package fr.openwide.alfresco.repo.remote.framework.web.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.Description.FormatStyle;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.Runtime;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WrappingWebScriptRequest;
import org.springframework.util.FileCopyUtils;

import fr.openwide.alfresco.repo.remote.framework.web.util.ThresholdOutputStream.ThresholdInputStream;

/**
 * Correction de {@link fr.openwide.alfresco.repo.remote.framework.web.util.web.scripts.BufferedRequest}.
 * 
 * @author asauvez
 */
public class BufferedRequest implements WrappingWebScriptRequest {
	private ThresholdOutputStreamFactory streamFactory;
	private WebScriptRequest req;
	private ThresholdInputStream thresholdInputStream;
	private InputStream contentStream;
	private BufferedReader contentReader;

	public BufferedRequest(WebScriptRequest req, ThresholdOutputStreamFactory streamFactory) {
		this.req = req;
		this.streamFactory = streamFactory;
	}

	private InputStream bufferInputStream() throws IOException {
		ThresholdOutputStream bufferStream = streamFactory.newOutputStream();

		try {
			FileCopyUtils.copy(req.getContent().getInputStream(), bufferStream);
		} catch (IOException e) {
			bufferStream.destroy(); // remove temp file
			throw e;
		}

		thresholdInputStream = (ThresholdInputStream) bufferStream.getInputStream();
		return thresholdInputStream;
	}

	public void rewind() throws IOException {
		if (contentStream != null) {
			thresholdInputStream.rewind();
		}
	}

	public void close() {
		if (contentStream != null) {
			try {
				contentStream.close();
			} catch (Exception e) {
			}
			contentStream = null;
		}
		if (contentReader != null) {
			try {
				contentReader.close();
			} catch (Exception e) {
			}
			contentReader = null;
		}
	}

	@Override
	public WebScriptRequest getNext() {
		return req;
	}

	@Override
	public boolean forceSuccessStatus() {
		return req.forceSuccessStatus();
	}

	@Override
	public String getAgent() {
		return req.getAgent();
	}

	@Override
	public Content getContent() {
		final Content wrapped = req.getContent();
		return new Content() {

			@Override
			public String getContent() throws IOException {
				return wrapped.getContent();
			}

			@Override
			public String getEncoding() {
				return wrapped.getEncoding();
			}

			@Override
			public String getMimetype() {
				return wrapped.getMimetype();
			}

			@Override
			public long getSize() {
				return wrapped.getSize();
			}

			@Override
			public InputStream getInputStream() {
				if (BufferedRequest.this.contentReader != null) {
					throw new IllegalStateException("Reader in use");
				}
				if (BufferedRequest.this.contentStream == null) {
					try {
						BufferedRequest.this.contentStream = bufferInputStream();
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				return BufferedRequest.this.contentStream;
			}

			@Override
			public BufferedReader getReader() throws IOException {
				if (BufferedRequest.this.contentStream != null) {
					throw new IllegalStateException("Input Stream in use");
				}
				if (BufferedRequest.this.contentReader == null) {
					String encoding = wrapped.getEncoding();
					InputStream in = bufferInputStream();
					BufferedRequest.this.contentReader = new BufferedReader(new InputStreamReader(in,
							encoding == null ? "ISO-8859-1" : encoding));
				}
				return BufferedRequest.this.contentReader;
			}
		};
	}

	@Override
	public String getContentType() {
		return req.getContentType();
	}

	@Override
	public String getContextPath() {
		return req.getContextPath();
	}

	@Override
	public String getExtensionPath() {
		return req.getExtensionPath();
	}

	@Override
	public String getFormat() {
		return req.getFormat();
	}

	@Override
	public FormatStyle getFormatStyle() {
		return req.getFormatStyle();
	}

	@Override
	public String getHeader(String name) {
		return req.getHeader(name);
	}

	@Override
	public String[] getHeaderNames() {
		return req.getHeaderNames();
	}

	@Override
	public String[] getHeaderValues(String name) {
		return req.getHeaderValues(name);
	}

	@Override
	public String getJSONCallback() {
		return req.getJSONCallback();
	}

	@Override
	public String getParameter(String name) {
		return req.getParameter(name);
	}

	@Override
	public String[] getParameterNames() {
		return req.getParameterNames();
	}

	@Override
	public String[] getParameterValues(String name) {
		return req.getParameterValues(name);
	}

	@Override
	public String getPathInfo() {
		return req.getPathInfo();
	}

	@Override
	public String getQueryString() {
		return req.getQueryString();
	}

	@Override
	public Runtime getRuntime() {
		return req.getRuntime();
	}

	@Override
	public String getServerPath() {
		return req.getServerPath();
	}

	@Override
	public String getServiceContextPath() {
		return req.getServiceContextPath();
	}

	@Override
	public Match getServiceMatch() {
		return req.getServiceMatch();
	}

	@Override
	public String getServicePath() {
		return req.getServicePath();
	}

	@Override
	public String getURL() {
		return req.getURL();
	}


	@Override
	public boolean isGuest() {
		return req.isGuest();
	}

	@Override
	public Object parseContent() {
		return req.parseContent();
	}
}
