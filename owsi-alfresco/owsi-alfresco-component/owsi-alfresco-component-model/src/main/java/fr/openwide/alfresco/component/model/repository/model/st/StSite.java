package fr.openwide.alfresco.component.model.repository.model.st;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.site.model.SiteVisibility;
import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.EnumTextPropertyModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmFolder;
import fr.openwide.alfresco.component.model.repository.model.sys.SysUndeletable;

public class StSite extends CmFolder {
	
	public StSite() {
		super(NameReference.create(StModel.NAMESPACE, "site"));
	}

	protected StSite(NameReference nameReference) {
		super(nameReference);
	}

	public static final String DASHBOARD_SITE_PRESET = "site-dashboard";
	public final TextPropertyModel sitePreset = PropertyModels.newText(this, StModel.NAMESPACE, "sitePreset");
	
	public final EnumTextPropertyModel<SiteVisibility> siteVisibility = PropertyModels.newTextEnum(this, StModel.NAMESPACE, "siteVisibility", SiteVisibility.class);
	
	public final SysUndeletable undeletable = addMandatoryAspect(SysModel.undeletable);
}
