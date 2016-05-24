package fr.openwide.alfresco.demo.web.application.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.api.module.identification.service.IdentificationService;
import fr.openwide.alfresco.app.web.download.model.NodeReferenceDownloadResponse;
import fr.openwide.alfresco.app.web.validation.model.AlertContainer;
import fr.openwide.alfresco.app.web.validation.model.ValidationResponse;
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

	
	/* 
	 * Display page
	 * */
	@RequestMapping(value="/folder", method=RequestMethod.GET)
	public String handleFolder(
			@RequestParam(value="nodeRef", required=false) NodeReference folderRef,
			Model model) {
		if (folderRef == null) {
			folderRef = identificationService.getByIdentifier(DemoModel.DEMO_ROOT_FOLDER).get();
		}
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.properties().name()
				.permissions().userPermissionDelete()
				.path()
				.permissions().userPermissionAddChildren();
		nodeScopeBuilder.assocs().childContains()
			.properties().name()
			.type()
			.properties().set(CmModel.content.content)
			.permissions().userPermissionDelete();
		nodeScopeBuilder.assocs().recursivePrimaryParent()
			.properties().name();
		BusinessNode folderNode = nodeModelService.get(folderRef, nodeScopeBuilder);
		NodeWrap folder = new NodeWrap(folderNode);
		
		model.addAttribute("folder", folder);
		model.addAttribute("filAriane", getFilAriane(folderNode));
		
		return "demo";
	}
	
	@RequestMapping(value = { "", "/" }, method = RequestMethod.GET)
	public String root() {
		return getRedirect("folder");
	}

	@RequestMapping(value="/file", method=RequestMethod.GET)
	public String handleFile(
			@RequestParam(value="nodeRef") NodeReference nodeRef,
			Model model) {
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.properties().name()
				.properties().description()
				.properties().title()
				.properties().set(CmModel.content.content)
				.permissions().userPermissionDelete();
		nodeScopeBuilder.assocs().recursivePrimaryParent()
		.properties().name();
		
		
		BusinessNode fileNode = nodeModelService.get(nodeRef, nodeScopeBuilder);
		NodeWrap file = new NodeWrap(fileNode);
		
		model.addAttribute("file", file);
		model.addAttribute("filAriane", getFilAriane(fileNode));
		
		return "displayFile";
	}

	
	/*
	 * Dowload content
	 * */
	@RequestMapping(method=RequestMethod.GET, value="/content/*")
	public NodeReferenceDownloadResponse downloadFile(
			@RequestParam("nodeRef") NodeReference fileRef,
			@RequestParam(value="forceDownload", defaultValue="false") boolean forceDownload,
			NodeReferenceDownloadResponse response) {
		response.nodeReference(fileRef)
				.attachment(forceDownload);
		return response;
	}
	
	
	
	/*
	 * Add content
	 */
	@RequestMapping(method=RequestMethod.POST, value="/ajax/add-folder")
	public ValidationResponse addFolder(
			@RequestParam("nodeRef") NodeReference folderRef,
			@RequestParam("folderName") String folderName,
			ValidationResponse response) {
		
		try {
			nodeModelService.createFolder(folderRef, folderName);
			response.getAlertContainer().addSuccess("ok.add.folder");
		} catch (Exception e) {
			response.getAlertContainer().addError("exception.add.folder");
		}

		return response;
	}
	
	
	@RequestMapping(method=RequestMethod.POST, value="/ajax/add-file")
	public ValidationResponse addFile(
			@RequestParam("nodeRef") NodeReference folderRef,
			@RequestParam("file") MultipartFile file,
			AlertContainer alertContainer,
			ValidationResponse response) {
		
		try {
			nodeModelService.createContent(folderRef, file);
			response.getAlertContainer().addSuccess("ok.add.file");
		} catch (Exception e) {
			response.getAlertContainer().addError("exception.add.file");
		}
		
		return response;
	}
	
	
	
	/*
	 * Delete content
	 */
	@RequestMapping(method=RequestMethod.GET, value="/ajax/delete")
	public ValidationResponse delete(
			@RequestParam("nodeRef") NodeReference folderRef,
			ValidationResponse response) {
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.assocs().recursivePrimaryParent()
				.properties().name();
		
		String parentAdresse = nodeModelService.get(folderRef, nodeScopeBuilder).assocs().primaryParent().getNodeReference().getReference();
		try {
			nodeModelService.delete(folderRef);
		} catch (Exception e) {
			response.getAlertContainer().addError("exception.delete");
//			throw e;
		}
		response.setRedirect("/demo/folder?nodeRef=" + parentAdresse);
		response.getAlertContainer().addSuccess("ok.delete");
		
		return response;
	}
	
	
	/*
	 * Functions
	 */
	
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
