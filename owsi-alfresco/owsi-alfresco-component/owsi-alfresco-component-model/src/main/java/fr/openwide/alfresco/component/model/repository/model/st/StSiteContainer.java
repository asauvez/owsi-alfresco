package fr.openwide.alfresco.component.model.repository.model.st;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.AspectModel;
import fr.openwide.alfresco.component.model.node.model.property.PropertyEnumeration;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;

public class StSiteContainer extends AspectModel {
	
	public StSiteContainer() {
		super(NameReference.create(StModel.NAMESPACE, "siteContainer"));
	}

	protected StSiteContainer(NameReference nameReference) {
		super(nameReference);
	}

	public enum SiteContainerType implements PropertyEnumeration {
		DOCUMENT_LIBRARY("documentLibrary");
		
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
