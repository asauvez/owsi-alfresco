package fr.openwide.alfresco.component.model.node.model;

import java.lang.reflect.Field;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;

/**
 * Permet de tester un model. Génére une liste d'erreurs/warnings.
 * 
 * @see ModelCheckerTesth
 */
public class XmlModelGenerator {
	
	public String getXmlModel(Class<?> model) throws Exception {
		NamespaceReference namespaceReference = (NamespaceReference) model.getField("NAMESPACE").get(null);
		String prefix = namespaceReference.getPrefix();
		String uri = namespaceReference.getUri();
		
		//profondeur 0
		StringBuilder xml = new StringBuilder("<?xml version=_\"1.0\" encoding=\"UTF-8\"?>\n")
			.append("<model name=\"").append(prefix).append(":").append(prefix).append("Model")
			.append("\" xmlns=\"http://www.alfresco.org/model/dictionary/1.0\">\n\n");
		
		//profondeur 1
		xml.append("	<imports>\n")
			.append("		<import uri=\"http://www.alfresco.org/model/dictionary/1.0\" prefix=\"d\" />\n")
			.append("		<import uri=\"http://www.alfresco.org/model/content/1.0\" prefix=\"cm\" />\n")
			.append("	</imports>\n\n");

		xml.append("	<namespaces>\n")
			.append("		<namespace uri=\"").append(uri).append("\" prefix=\"").append(prefix).append("\" />\n")
			.append("	</namespaces>\n\n");

		xml.append("	<types>\n");
		for (Field field : model.getFields()) {
			Object value = field.get(null);
			if (value instanceof TypeModel) {
				//profondeur 2
				ContainerModel container = (ContainerModel) value;
				xml.append(container.getXmlModel("type", 2));
			}
		}
		xml.append("	</types>\n\n");
		
		
		xml.append("	<aspects>\n");
		for (Field field : model.getFields()) {
			Object value = field.get(null);
			if (value instanceof AspectModel) {
				//profondeur 2
				ContainerModel container = (ContainerModel) value;
				xml.append(container.getXmlModel("aspect", 2));
				
			}
		}
		xml.append("	</aspects>\n\n");
		
		xml.append("</model>\n");
		
		return xml.toString();
	}
	
}
