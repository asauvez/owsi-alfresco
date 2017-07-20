package fr.openwide.alfresco.repo.core.node.web.script;

import java.io.InputStream;

public interface NodeContentCallback {

	void doWithInputStream(InputStream inputStream);

}