package fr.openwide.alfresco.repo.contentstoreexport.service;

import java.io.IOException;
import java.io.OutputStream;

import fr.openwide.alfresco.repo.contentstoreexport.model.ContentStoreExportParams;

public interface ContentStoreExportService {

	void export(OutputStream outputStream, ContentStoreExportParams params) throws IOException;
}
