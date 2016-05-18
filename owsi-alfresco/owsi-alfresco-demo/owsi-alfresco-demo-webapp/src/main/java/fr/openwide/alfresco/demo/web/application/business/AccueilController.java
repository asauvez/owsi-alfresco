package fr.openwide.alfresco.demo.web.application.business;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.app.web.download.model.NodeReferenceDownloadResponse;
import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.demo.web.application.framework.spring.controller.BusinessController;

@Controller
public class AccueilController extends BusinessController {

	public static final String LOGIN_URL = "/security/login";
	public static final String REFRESH_URL = "/security/refresh";

	@Autowired
	private NodeModelService nodeModelService;
	@Autowired
	private IdentificationService identificationService;

	@RequestMapping(value="/folder", method=RequestMethod.GET)
	public String handleFolder(
			@RequestParam(value="nodeRef", required=false) NodeReference folderRef,
			Model model) {
		if (folderRef == null) {
			folderRef = identificationService.getByIdentifier(DemoModel.DEMO_ROOT_FOLDER).get();
		}
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.properties().name()
				.path();
		nodeScopeBuilder.assocs().childContains()
			.properties().name()
			.type()
			.properties().set(CmModel.content.content);
		nodeScopeBuilder.assocs().recursivePrimaryParent()
			.properties().name();
		BusinessNode folderNode = nodeModelService.get(folderRef, nodeScopeBuilder);
		NodeWrap folder = new NodeWrap(folderNode);
		
		model.addAttribute("folder", folder);
		
		return "demo";
	}
	
//	@PreAuthorize(BusinessPermissionConstants.ROLE_UTILISATEUR)
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public String root() {
		return getRedirect("folder");
	}

	@RequestMapping(method=RequestMethod.GET, value=LOGIN_URL)
	public String getLoginPage(HttpSession session, AlertContainer alertContainer) {
		AuthenticationException authenticationException = (AuthenticationException) session.getAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		if (authenticationException != null) {
			if (authenticationException instanceof AuthenticationServiceException) {
				alertContainer.addError("login.repositoryNotFound");
			} else if (authenticationException instanceof BadCredentialsException) {
				alertContainer.addError("login.invalidPassword");
			} else {
				throw authenticationException;
			}
			session.removeAttribute(WebAttributes.AUTHENTICATION_EXCEPTION);
		}
		return "login";//Views.Security.login;
	}

	@RequestMapping(method=RequestMethod.GET, value="/content/*")
	public NodeReferenceDownloadResponse downloadFile(
			@RequestParam("nodeRef") NodeReference fileRef,
			NodeReferenceDownloadResponse response) {
		response.nodeReference(fileRef);
		return response;
	}
}
