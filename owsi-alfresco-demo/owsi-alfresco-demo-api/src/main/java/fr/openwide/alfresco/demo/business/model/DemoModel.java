package fr.openwide.alfresco.demo.business.model;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;
import fr.openwide.alfresco.demo.business.model.demo.DemoAspect;


public interface DemoModel {
	
	NamespaceReference NAMESPACE = NamespaceReference.create("demo", "http://openwide.fr/modele/demo");
	
	NameReference DEMO_ROOT_FOLDER = NameReference.create(NAMESPACE, "demoRootFolder");
	
	DemoAspect demoAspect = new DemoAspect();
}
