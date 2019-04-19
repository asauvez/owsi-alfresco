package fr.openwide.alfresco.repo.core.admin.web.script;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.module.ModuleComponentHelper;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.VersionNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;

/**
 * http://localhost:8080/alfresco/service/owsi/admin/setModuleCurrentVersion?module={moduleId}&version={version}
 * http://localhost:8080/alfresco/service/owsi/admin/setModuleCurrentVersion?module=owsi-alfresco-repo-contentstoreexport&version=0.4.0
 * 
 * Si Alfresco ne démarre pas, vous pouvez aussi utiliser @see ResetModuleVersionServiceImpl
 */
@GenerateWebScript(
		url="/owsi/admin/setModuleCurrentVersion?module={moduleId}&version={version}",
		description="Modifie la version courante enregistrée dans Alfresco pour un module. A utiliser avec précaution par un administrateur.",
		formatDefault="html",
		family=OwsiSwaggerWebScript.WS_FAMILY,
		useViewFile=true)
public class SetModuleCurrentVersionWebScript extends DeclarativeWebScript {
	
	private static final String MODULE_PARAM = "module";
	private static final String VERSION_PARAM = "version";
	
	private static final Logger logger = LoggerFactory.getLogger(SetModuleCurrentVersionWebScript.class);
	
	@Autowired
	private NodeService nodeService;
	
	@Override
	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache) {
		String _module = req.getParameter(MODULE_PARAM);
		String _version = req.getParameter(VERSION_PARAM);
		logger.info("ATTENTION, changement de la version courante du module "+_module);

//		NodeRef nodeRef = getNodeRefFromPath("/sys:system-registry/module:modules/module:"+_module);
		NodeRef nodeRef = nodeService.getRootNode(new StoreRef("system", "system"));
		nodeRef = getSystemChild(nodeRef, "{http://www.alfresco.org/model/system/1.0}system-registry");
		nodeRef = getSystemChild(nodeRef, "{http://www.alfresco.org/system/modules/1.0}modules");
		nodeRef = getSystemChild(nodeRef, "{http://www.alfresco.org/system/modules/1.0}"+_module);
		
		if(nodeRef == null){
			setStatusForBadRequest(status, 404, "Pas de registre pour le module "+_module, null);
			return null;
		}
		QName propName = QName.createQName(ModuleComponentHelper.URI_MODULES_1_0, "currentVersion");
		Serializable oldVersion = nodeService.getProperty(nodeRef, propName);
		if (logger.isInfoEnabled()) {
			logger.info("Registre du module trouvé "+nodeRef.toString());
			logger.info("Version actuelle : "+oldVersion);
		}
		VersionNumber versionNumber;
		try{
			versionNumber = new VersionNumber(_version);
		} catch(AlfrescoRuntimeException e) {
			setStatusForBadRequest(status, 400, "Numéro de version incorrect "+_version, e);
			return null;
		}
		nodeService.setProperty(nodeRef, propName, versionNumber);
		String msg = "La version courante enregistrée pour le module "+_module+" a été changée en "+_version+" (ancienne valeur : "+oldVersion+")";
		logger.info(msg);
		Map<String, Object> model = new HashMap<>();
		model.put("_message", msg);
		return model;
	}
	
	private NodeRef getSystemChild(NodeRef nodeRef, String childName) {
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName(childName));
		return (! childAssocs.isEmpty()) ? childAssocs.get(0).getChildRef() : null;
	}

	private void setStatusForBadRequest(Status status, int code, String message, Throwable e) {
		status.setCode(code);
		status.setMessage(message);
		status.setException(e);
		status.setRedirect(true);
	}

}

