package fr.openwide.alfresco.app.core.node.binding;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;

import fr.openwide.alfresco.api.core.node.binding.NodeContentDeserializer;
import fr.openwide.alfresco.api.core.node.model.RepositoryNode;
import fr.openwide.alfresco.api.core.remote.model.NameReference;

public abstract class ZipFileRepositoryContentDeSerializer 
		implements NodeContentDeserializer<String> {

	private NameReference propertyName = NameReference.create("cm", "name");
	private final ZipOutputStream outputStream;
	
	public ZipFileRepositoryContentDeSerializer(File file) throws FileNotFoundException {
		this(new BufferedOutputStream(new FileOutputStream(file)));
	}
	public ZipFileRepositoryContentDeSerializer(OutputStream outputStream) {
		this(new ZipOutputStream(outputStream));
	}
	public ZipFileRepositoryContentDeSerializer(ZipOutputStream outputStream) {
		this.outputStream = outputStream;
	}
	
	public void setPropertyName(NameReference propertyName) {
		this.propertyName = propertyName;
	}
	
	@Override
	public String deserialize(RepositoryNode node, NameReference contentProperty, InputStream inputStream) throws IOException {
		String fileName = node.getProperty(propertyName, String.class);
		if (fileName == null) {
			throw new IllegalStateException("Node " + node.getNodeReference() + " doesn't have a " + propertyName);
		}
		
		outputStream.putNextEntry(new ZipEntry(fileName));
		IOUtils.copy(inputStream, outputStream);
		outputStream.closeEntry();
		
		return fileName;
	}

}
