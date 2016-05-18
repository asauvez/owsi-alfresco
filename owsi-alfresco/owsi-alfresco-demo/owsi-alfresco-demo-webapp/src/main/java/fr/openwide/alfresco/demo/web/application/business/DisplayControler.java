package fr.openwide.alfresco.demo.web.application.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.app.web.download.model.NodeReferenceDownloadResponse;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.demo.business.model.DemoModel;
import fr.openwide.alfresco.demo.web.application.framework.spring.controller.BusinessController;

@Controller
public class DisplayControler extends BusinessController {

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
		model.addAttribute("filAriane", getFilAriane(folderNode));
		
		return "demo";
	}
	
//	@PreAuthorize(BusinessPermissionConstants.ROLE_UTILISATEUR)
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public String root() {
		return getRedirect("folder");
	}


	@RequestMapping(method=RequestMethod.GET, value="/content/*")
	public NodeReferenceDownloadResponse downloadFile(
			@RequestParam("nodeRef") NodeReference fileRef,
			NodeReferenceDownloadResponse response) {
		response.nodeReference(fileRef);
		return response;
	}
	
	@RequestMapping(value="/file", method=RequestMethod.GET)
	public String handleFile(
			@RequestParam(value="nodeRef") NodeReference nodeRef,
			Model model) {
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.properties().name()
				.properties().description()
				.properties().title()
				.properties().set(CmModel.content.content);
		nodeScopeBuilder.assocs().recursivePrimaryParent()
			.properties().name();
		

		BusinessNode fileNode = nodeModelService.get(nodeRef, nodeScopeBuilder);
		NodeWrap file = new NodeWrap(fileNode);
		
		model.addAttribute("file", file);
		model.addAttribute("filAriane", getFilAriane(fileNode));
		
		
		
		return "displayFile";
	}
	
	private List<NodeWrap> getFilAriane(BusinessNode nodeReference){
		List<NodeWrap> filAriane = new ArrayList<NodeWrap>();
		
		getFilAriane(nodeReference.assocs().getPrimaryParent(), filAriane);
		
		return filAriane;
	}
	
	private void getFilAriane(BusinessNode nodeReference, List<NodeWrap> filAriane){
		NodeWrap wrap = new NodeWrap(nodeReference);
		if (nodeReference.assocs().getPrimaryParent() != null){
			getFilAriane(nodeReference.assocs().getPrimaryParent(), filAriane);
			filAriane.add(wrap);
		}
	}
}
