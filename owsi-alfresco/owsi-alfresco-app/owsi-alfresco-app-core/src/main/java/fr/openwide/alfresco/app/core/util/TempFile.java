package fr.openwide.alfresco.app.core.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 * Permet d'avoir des fichiers temporaires gérés en try-with-resource.
 * Les outputstreams sont flushés dès que l'on demande un inputstream.
 * Toutes les streams sont fermés à la fin du bloc. Le fichier est effacé.
 * 
 * try (TempFile original = new TempFile("temp", ".pdf")) {
 *		IOUtils.copy(is, original.newOutputStream());
 *		pdfMergeService.merge(ByteStreams.nullOutputStream(), original.newInputStream());
 * }
 */
public class TempFile implements Closeable {

	private final File file;
	private final Set<Flushable> flushables = new HashSet<>();
	private final Set<Closeable> closeables = new HashSet<>();
	
	public TempFile(String prefix, String suffix) {
		try {
			this.file = File.createTempFile(prefix, suffix);
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public File getFile() {
		return file;
	}
	public OutputStream newOutputStream() throws IOException {
		OutputStream stream = new BufferedOutputStream(new FileOutputStream(file));
		closeables.add(stream);
		flushables.add(stream);
		return stream;
	}
	public InputStream newInputStream() throws IOException {
		for (Flushable flushable : flushables) {
			flushable.flush();
		}
		InputStream stream = new BufferedInputStream(new FileInputStream(file));
		closeables.add(stream);
		return stream;
	}
	
	@Override
	public void close() {
		for (Flushable flushable : flushables) {
			try {
				flushable.flush();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		for (Closeable closeable : closeables) {
			IOUtils.closeQuietly(closeable);
		}
		FileUtils.deleteQuietly(file);
	}
}
