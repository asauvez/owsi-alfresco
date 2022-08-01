package fr.openwide.alfresco.component.model.repository.model.st;

import org.alfresco.service.namespace.QName;

import fr.openwide.alfresco.component.model.node.model.property.PropertyModels;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.repository.model.StModel;
import fr.openwide.alfresco.component.model.repository.model.SysModel;
import fr.openwide.alfresco.component.model.repository.model.cm.CmFolder;
import fr.openwide.alfresco.component.model.repository.model.sys.SysUndeletable;

public class StSite extends CmFolder {
	
	public StSite() {
		super(StModel.NAMESPACE.createQName("site"));
	}

	protected StSite(QName qName) {
		super(qName);
	}

	public static final String DASHBOARD_SITE_PRESET = "site-dashboard";
	public final TextPropertyModel sitePreset = PropertyModels.newText(this, StModel.NAMESPACE, "sitePreset");
	
	public final TextPropertyModel siteVisibility = PropertyModels.newText(this, StModel.NAMESPACE, "siteVisibility");
	
	public final SysUndeletable undeletable = addMandatoryAspect(SysModel.undeletable);
}
