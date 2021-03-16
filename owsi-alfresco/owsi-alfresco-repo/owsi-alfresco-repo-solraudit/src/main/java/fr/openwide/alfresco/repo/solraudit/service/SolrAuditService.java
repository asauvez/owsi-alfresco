package fr.openwide.alfresco.repo.solraudit.service;

import java.io.PrintWriter;

public interface SolrAuditService {

	void generateAudit(PrintWriter out);

	void storeAudit();

}
