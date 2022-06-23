package fr.openwide.alfresco.repo.solraudit.service;

import java.io.PrintWriter;
import java.util.function.Consumer;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;

public interface SolrAuditService {

	void generateAudit(PrintWriter out);
	void generateAudit(PrintWriter out, StoreRef storeRef);
	
	void storeAudit();
	void storeAudit(boolean includeTrashcan);
	
	
	void registerPropertiesPolicy(QName container, Consumer<NodeRef> consumer);
	
	void registerDateGroup(QName container, QName propertyText);
	void registerDateGroup(QName container, QName propertyDate, QName propertyString, String format);

	void registerLogSize(QName container, QName propertyText);
}
