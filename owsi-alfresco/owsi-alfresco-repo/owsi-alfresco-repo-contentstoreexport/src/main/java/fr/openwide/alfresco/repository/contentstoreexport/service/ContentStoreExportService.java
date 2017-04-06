package fr.openwide.alfresco.repository.contentstoreexport.service;

import java.io.IOException;
import java.io.OutputStream;

public interface ContentStoreExportService {
	void export(OutputStream outPutStream) throws IOException;
}
