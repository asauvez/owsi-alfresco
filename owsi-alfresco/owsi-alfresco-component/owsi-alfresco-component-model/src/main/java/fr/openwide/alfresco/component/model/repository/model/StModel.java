package fr.openwide.alfresco.component.model.repository.model;

import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.component.model.repository.model.st.StCustomSiteProperties;
import fr.openwide.alfresco.component.model.repository.model.st.StSite;
import fr.openwide.alfresco.component.model.repository.model.st.StSiteContainer;
import fr.openwide.alfresco.component.model.repository.model.st.StSites;

public interface StModel {

	// https://svn.alfresco.com/repos/alfresco-open-mirror/alfresco/HEAD/root/projects/repository/config/alfresco/model/siteModel.xml
	NamespaceReference NAMESPACE = NamespaceReference.create("st", "http://www.alfresco.org/model/site/1.0");
	NamespaceReference CUSTOM_PROPERTY_NAMESPACE = NamespaceReference.create("stcp", "http://www.alfresco.org/model/sitecustomproperty/1.0");

	// ---- Aspects

	StSiteContainer siteContainer = new StSiteContainer();

	StCustomSiteProperties customSiteProperties = new StCustomSiteProperties();
	
	// ---- Types

	StSite site = new StSite();
	
	StSites sites = new StSites();
}
