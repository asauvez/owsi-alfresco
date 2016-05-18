package fr.openwide.alfresco.demo.web.application.business;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.openwide.alfresco.api.core.remote.model.NodeReference;
import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.node.service.NodeModelService;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.demo.web.application.framework.spring.controller.BusinessController;

@Controller
public class DisplayFileController extends BusinessController{
	
	@Autowired
	private NodeModelService nodeModelService;

	@RequestMapping(value="/file", method=RequestMethod.GET)
	public String handleFile(
			@RequestParam(value="nodeRef") NodeReference nodeRef,
			Model model) {
		
		NodeScopeBuilder nodeScopeBuilder = new NodeScopeBuilder()
				.properties().name()
				.properties().description()
				.properties().title()
				.properties().set(CmModel.content.content);
		

		BusinessNode fileNode = nodeModelService.get(nodeRef, nodeScopeBuilder);
		NodeWrap file = new NodeWrap(fileNode);
		
		model.addAttribute("file", file);
		
		
		return "afficheFile";
	}
}
