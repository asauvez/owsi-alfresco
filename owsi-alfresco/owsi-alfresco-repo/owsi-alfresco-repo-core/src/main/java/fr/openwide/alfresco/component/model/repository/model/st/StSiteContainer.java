package fr.openwide.alfresco.component.model.repository.model.st;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyEnumeration;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;

public class StSiteContainer extends AspectModel {
	
	public StSiteContainer() {
		super(StModel.NAMESPACE.createQName("siteContainer"));
	}

	protected StSiteContainer(QName qName) {
		super(qName);
	}

	public enum SiteContainerType implements PropertyEnumeration {
		DOCUMENT_LIBRARY("documentLibrary"),
		WIKI("wiki"),
		LINKS("links"),
		DISCUSSIONS("discussions"),
		DATALISTS("dataLists"),
		BLOG("blog"),
		CALENDAR("calendar"),
		OTHER(PropertyEnumeration.OTHER_VALUES);
		
		private final String code;
		private SiteContainerType(String code) {
			this.code = code;
		}
		@Override
		public String getCode() {
			return code;
		}
	}
	public final EnumTextPropertyModel<SiteContainerType> componentId = PropertyModels.newTextEnum(this, StModel.NAMESPACE, "componentId", SiteContainerType.class);
}
