package fr.openwide.alfresco.repository.core.node.web.script;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.openwide.alfresco.api.core.node.binding.content.ZipIterator;
import fr.openwide.alfresco.api.core.node.model.ContentPropertyWrapper;


public class NodeContentHolder {

	private static final Logger LOGGER = LoggerFactory.getLogger(NodeContentHolder.class);
	
	private ContentPropertyWrapper wrapper;
	private NodeContentCallback callback;

	public NodeContentHolder(ContentPropertyWrapper wrapper) {
		this.wrapper = wrapper;
	}
	
	/**
	 * Soit l'appel se fait sur le contenu suivant dans le Zip, auquel cas on le fait tout de suite, soit ce n'est pas
	 * le fichier suivant, et on le fera à la fin de la requête. 
	 */
	public void setContentCallback(NodeContentCallback callback) {
		ZipIterator zipIterator = wrapper.getZipIterator();
		if (Integer.toString(wrapper.getContentId()).equals(zipIterator.getCurrentEntry().getName())) {
			callback.doWithInputStream(zipIterator.getInputStream());
			zipIterator.next();
			
		} else {
			LOGGER.warn("Access to node content not done in serialization order. Fallback to callback done at the end of the query");
			this.callback = callback;
		}
	}

	public NodeContentCallback getCallback() {
		return callback;
	}
}