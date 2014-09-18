package fr.openwide.alfresco.app.core.node.binding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentDeserializer;
import fr.openwide.alfresco.repository.api.node.binding.RepositoryContentSerializer;
import fr.openwide.alfresco.repository.api.node.model.RepositoryNode;
import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public class InputStreamRepositoryContentSerializer 
	implements RepositoryContentSerializer<InputStream>, RepositoryContentDeserializer<InputStream> {

	public static final InputStreamRepositoryContentSerializer INSTANCE = new InputStreamRepositoryContentSerializer();

	protected InputStreamRepositoryContentSerializer() {}

	@Override
	public void serialize(RepositoryNode node, NameReference contentProperty, InputStream content, OutputStream outputStream)
			throws IOException {
		try (InputStream inputStream = content) {;
			IOUtils.copy(inputStream, outputStream);
		}
	}

	@Override
	public InputStream deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		final File tempFile = File.createTempFile(InputStreamRepositoryContentSerializer.class.getSimpleName(), ".tmp");
		Files.copy(inputStream, tempFile.toPath());
		
		return new FilterInputStream(null) {
			private void checkOpen() throws FileNotFoundException {
				if (in == null) {
					in = new FileInputStream(tempFile);
				}
			}
			@Override
			public int read() throws IOException {
				checkOpen();
				return super.read();
			}
			@Override
			public int read(byte[] b, int off, int len) throws IOException {
				checkOpen();
				return super.read(b, off, len);
			}
			@Override
			public void close() throws IOException {
				if (in != null) {
					super.close();
				}
				tempFile.delete();
			}
			@Override
			protected void finalize() {
				try {
					close();
				} catch (IOException e) {
					throw new IllegalStateException(e);
				}
			}
		};
	}
}
