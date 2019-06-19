package fr.openwide.alfresco.repo.contentstoreexport.service;

import java.io.IOException;
import java.io.OutputStream;

public interface ContentStoreExportService {
	void export(OutputStream outPutStream, String paths, String queries, String nodeRefs, boolean exportContent) throws IOException;
}
