package fr.openwide.alfresco.component.model.node.model;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.association.AssociationModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModel;

public class ContainerModel extends Model {

	private Map<QName, PropertyModel<?>> properties = new HashMap<>();

	private Map<QName, AspectModel> mandatoryAspects = new HashMap<>();
	
	public ContainerModel(QName qName) {
		super(qName);
	}

	public Map<QName, PropertyModel<?>> getProperties() {
		return Collections.unmodifiableMap(properties);
	}
	public PropertyModel<?> getProperty(QName qName) {
		return properties.get(qName);
	}
	public void addProperty(PropertyModel<?> property) {
		properties.put(property.getQName(), property);
	}

	public Map<QName, AspectModel> getMandatoryAspects() {
		return Collections.unmodifiableMap(mandatoryAspects);
	}
	public <A extends AspectModel> A addMandatoryAspect(A aspect) {
		if (aspect == null) {
			throw new IllegalStateException("In your model, declare mandatory aspects before declaring them mandatory in another aspect.");
		}
		
		mandatoryAspects.put(aspect.getQName(), aspect);
		for (PropertyModel<?> property : aspect.getProperties().values()) {
			properties.put(property.getQName(), property);
		}
		return aspect;
	}
	
	/**
	 * Adds all properties and aspects from the source model to the destination.
	 */
	protected static <T extends ContainerModel> void copy(T source, T destination) {
		for (PropertyModel<?> property : source.getProperties().values()) {
			destination.addProperty(property);
		}
		for (AspectModel mandatoryAspect : source.getMandatoryAspects().values()) {
			destination.addMandatoryAspect(mandatoryAspect);
		}
	}
	
	public String getXmlModel(String containerType) throws Exception {
		return getXmlModel(containerType,0);
	}

	public String getXmlModel(String containerType, int profondeur) throws Exception {
		StringBuilder xml = new StringBuilder();
		StringBuilder tabulation = new StringBuilder();
		
		for (int i = 0; i < profondeur; i++){
			tabulation.append("	");
		}
		
		xml.append(tabulation.toString()).append("<").append(containerType).append(" name=\"").append(this.getQName().toPrefixString()).append("\">\n");
			this.getXmlProperties(xml, this, profondeur + 1);
		xml.append(tabulation.toString()).append("</").append(containerType).append(">\n");
		
		return xml.toString();
	}
	
	
	public void getXmlProperties(StringBuilder xml, ContainerModel container) throws Exception {
		getXmlProperties(xml, container, 0);
	}
	
	public void getXmlProperties(StringBuilder xml, ContainerModel container, int profondeur) throws Exception {
		StringBuilder properties = new StringBuilder();
		StringBuilder associations = new StringBuilder();
		StringBuilder tabulation = new StringBuilder();
		
		for (int i = 0; i < profondeur; i++){
			tabulation.append("	");
		}
		
		try {
			ContainerModel parentModel = (ContainerModel) container.getClass().getSuperclass().getConstructor().newInstance();
			xml.append(tabulation.toString()).append("<parent>").append(parentModel.getQName().toPrefixString()).append("</parent>\n");
		} catch (NoSuchMethodException ex) {
			// Ignore, on doit être à la racine
		}
		
		for (Field field : container.getClass().getFields()) {
			if (field.getDeclaringClass() == container.getClass()) {
				Object value = field.get(container);
				if (value instanceof PropertyModel) {
					PropertyModel<?> property = (PropertyModel<?>) value;
					properties.append(property.getXmlModel(profondeur + 1));
				}
				else if (value instanceof AssociationModel) {
					AssociationModel asso = (AssociationModel) value;
					associations.append(asso.getXmlModel(profondeur +1));
				}
			}
			
		}
		
		if(properties.length() != 0){
			xml.append("			<properties>\n")
				.append(properties.toString())
				.append("			</properties>\n");
		}
		
		if(associations.length() != 0){
			xml.append("			<associations>\n")
				.append(associations.toString())
				.append("			</associations>\n");
		}
	}
}
