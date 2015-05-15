package fr.openwide.alfresco.repository.core.node.web.script;

import java.io.InputStream;

import fr.openwide.alfresco.api.core.remote.model.NameReference;

public interface NodeContentCallback {

	void doWithInputStream(NameReference contentProperty, InputStream inputStream);

}