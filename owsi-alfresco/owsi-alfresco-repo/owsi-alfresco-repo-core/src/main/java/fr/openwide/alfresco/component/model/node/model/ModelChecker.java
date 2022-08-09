package fr.openwide.alfresco.component.model.node.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

/**
 * Permet de tester un model. Génére une liste d'erreurs/warnings.
 * 
 * @see ModelCheckerTesth
 */
public class ModelChecker {
	
	private List<String> errors = new ArrayList<>();
	
	public void checkModel(Class<?> model) throws Exception {
		String nameSpace = ((NamespaceReference) model.getField("NAMESPACE").get(null)).getPrefix();
		
		for (Field field : model.getFields()) {
			Object value = field.get(null);
			if (value instanceof ContainerModel) {
				ContainerModel container = (ContainerModel) value;
				String prefix = StringUtils.substringBefore(container.getQName().getPrefixString(), ":");
				if (! prefix.equals(nameSpace)) {
					errors.add(model.getSimpleName() + " : Field NAMESPACE " + nameSpace + " is not the same as the container namespace " + container.getQName().toPrefixString());
				}
				if (! container.getQName().getLocalName().replace("-", "_").equals(field.getName())) {
					errors.add(model.getSimpleName() + " : Field name " + field.getName() + " is not the same as the container name " + container.getQName().getLocalName());
				}
				checkContainer(nameSpace, container);
			}
		}
	}
	
	private void checkContainer(String nameSpace, ContainerModel container) throws Exception {
		for (Field field : container.getClass().getFields()) {
			if (field.getDeclaringClass() == container.getClass()) {
				Object value = field.get(container);
				if (value instanceof PropertyModel) {
					PropertyModel<?> property = (PropertyModel<?>) value;
					String prefix = StringUtils.substringBefore(property.getQName().getPrefixString(), ":");
					if (! prefix.equals(nameSpace)) {
						errors.add(container.getClass().getSimpleName() + " : Field NAMESPACE " + nameSpace + " is not the same as the property namespace " + property.getQName().toPrefixString());
					}
					if (! property.getQName().getLocalName().replace("-", "_").equals(field.getName())) {
						errors.add(container.getClass().getSimpleName() + " : Field name " + field.getName() + " is not the same as the property name " + property.getQName().getLocalName());
					}
				}
			}
		}
	}
	
	public String getErrors() {
		return errors.toString().replace(", ", "\n").replace("[", "").replace("]", "");
	}
	
}
