package fr.openwide.alfresco.repo.solraudit.service;

import java.io.PrintWriter;

import org.alfresco.service.cmr.repository.StoreRef;

public interface SolrAuditService {

	void generateAudit(PrintWriter out);
	void generateAudit(PrintWriter out, StoreRef storeRef);

	void storeAudit();
	void storeAudit(boolean includeTrashcan);

}
