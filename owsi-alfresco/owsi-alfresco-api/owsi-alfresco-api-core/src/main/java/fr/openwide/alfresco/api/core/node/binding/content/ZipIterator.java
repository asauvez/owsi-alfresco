package fr.openwide.alfresco.api.core.node.binding.content;

import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ZipIterator {
	
	private final ZipInputStream zipInputStream;
	private final InputStream nonClosing;
	private ZipEntry currentEntry;
	
	public ZipIterator(InputStream inputStream) {
		this.zipInputStream = new ZipInputStream(inputStream);
		nonClosing = NonClosingStreamUtils.nonClosing(zipInputStream);
		try {
			currentEntry = zipInputStream.getNextEntry();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public ZipEntry getCurrentEntry() {
		return currentEntry;
	}
	
	public boolean hasNext() {
		return currentEntry != null;
	}
	
	public void next() {
		try {
			zipInputStream.closeEntry();
			currentEntry = zipInputStream.getNextEntry();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public void closeLastEntry() {
		try {
			zipInputStream.closeEntry();
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public InputStream getInputStream() {
		return nonClosing;
	}
}
