package fr.openwide.alfresco.repo.core.admin.web.script;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.List;

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
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;

/**
 * http://localhost:8080/alfresco/service/owsi/admin/setModuleCurrentVersion?module={moduleId}&version={version}
 * http://localhost:8080/alfresco/service/owsi/admin/setModuleCurrentVersion?module=owsi-alfresco-repo-contentstoreexport&version=0.4.0
 * 
 * Si Alfresco ne démarre pas, vous pouvez aussi utiliser @see ResetModuleVersionServiceImpl
 */
@GenerateWebScript(
		url="/owsi/admin/setModuleCurrentVersion?module={moduleId}&version={version}",
		description="Modifie la version courante enregistrée dans Alfresco pour un module. A utiliser avec précaution par un administrateur.",
		formatDefaultEnum=GenerateWebScriptFormatDefault.HTML,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		authentication=GenerateWebScriptAuthentication.ADMIN)
public class SetModuleCurrentVersionWebScript extends AbstractWebScript {
	
	private static final String MODULE_PARAM = "module";
	private static final String VERSION_PARAM = "version";
	
	private static final Logger logger = LoggerFactory.getLogger(SetModuleCurrentVersionWebScript.class);
	
	@Autowired
	private NodeService nodeService;
	
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setContentType("text/plain");

		String _module = req.getParameter(MODULE_PARAM);
		String _version = req.getParameter(VERSION_PARAM);
		logger.info("ATTENTION, changement de la version courante du module "+_module);

//		NodeRef nodeRef = getNodeRefFromPath("/sys:system-registry/module:modules/module:"+_module);
		NodeRef nodeRef = nodeService.getRootNode(new StoreRef("system", "system"));
		nodeRef = getSystemChild(nodeRef, "{http://www.alfresco.org/model/system/1.0}system-registry");
		nodeRef = getSystemChild(nodeRef, "{http://www.alfresco.org/system/modules/1.0}modules");
		nodeRef = getSystemChild(nodeRef, "{http://www.alfresco.org/system/modules/1.0}"+_module);
		
		if(nodeRef == null){
			setStatusForBadRequest(res, 404, "Pas de registre pour le module "+_module, null);
			return;
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
			setStatusForBadRequest(res, 400, "Numéro de version incorrect "+_version, e);
			return;
		}
		nodeService.setProperty(nodeRef, propName, versionNumber);
		String msg = "La version courante enregistrée pour le module "+_module+" a été changée en "+_version+" (ancienne valeur : "+oldVersion+")";
		logger.info(msg);
		
		try (Writer writer = res.getWriter()) {
			writer.write(msg);
		}
	}
	
	private NodeRef getSystemChild(NodeRef nodeRef, String childName) {
		List<ChildAssociationRef> childAssocs = nodeService.getChildAssocs(nodeRef, ContentModel.ASSOC_CHILDREN, QName.createQName(childName));
		return (! childAssocs.isEmpty()) ? childAssocs.get(0).getChildRef() : null;
	}

	private void setStatusForBadRequest(WebScriptResponse res, int status, String message, Throwable e) throws IOException {
		res.setStatus(status);
		try (PrintWriter writer = new PrintWriter(res.getWriter())) {
			writer.println(message);
			e.printStackTrace(writer);
		}
	}

}

