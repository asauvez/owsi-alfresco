package fr.openwide.alfresco.repository.core.node.web.script;

import java.io.InputStream;

import fr.openwide.alfresco.repository.api.remote.model.NameReference;

public interface NodeContentCallback {

	void doWithInputStream(NameReference contentProperty, InputStream inputStream);

}