package fr.openwide.alfresco.demo.web.application.business;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import fr.openwide.alfresco.component.model.node.model.BusinessNode;
import fr.openwide.alfresco.component.model.node.model.NodeScopeBuilder;
import fr.openwide.alfresco.component.model.repository.model.CmModel;
import fr.openwide.alfresco.component.model.search.model.SearchQueryBuilder;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.component.model.search.service.NodeSearchModelService;
import fr.openwide.alfresco.demo.web.application.framework.spring.controller.BusinessController;

@Controller
public class SearchControler extends BusinessController {

	@Autowired
	private NodeSearchModelService nodeSearchModelService;
	
	@RequestMapping(method=RequestMethod.GET, value="/search")
	public String search(@RequestParam("q") String query, Model model) {
		List<BusinessNode> list = nodeSearchModelService.search(new SearchQueryBuilder()
			.restriction(new RestrictionBuilder()
				.or()
					.match(CmModel.object.name, query).of()
					.match(CmModel.titled.title, query).of()
					.match(CmModel.titled.description, query).of()
					.match(CmModel.content.content, query).of()
					.of())
			.sort().sortByName(),
			new NodeScopeBuilder()
				.properties().name());
		
		List<NodeWrap> results = new ArrayList<NodeWrap>();
		for (BusinessNode i : list){
			results.add(new NodeWrap(i));
		}
		model.addAttribute("results", results);
		
		return "displaySearch";
}
	
}
