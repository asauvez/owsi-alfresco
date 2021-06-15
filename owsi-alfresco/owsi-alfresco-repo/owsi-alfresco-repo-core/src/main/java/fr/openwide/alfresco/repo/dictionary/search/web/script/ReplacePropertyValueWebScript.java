package fr.openwide.alfresco.repo.dictionary.search.web.script;

import java.io.IOException;
import java.util.function.Consumer;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.node.model.property.single.TextPropertyModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.dictionary.search.model.BatchSearchQueryBuilder;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;

/**
 * http://localhost:8080/alfresco/s/owsi/batch/replacePropertyValue?property=exif:manufacturer&old=OLYMPUS OPTICAL CO.,LTD&new=Manufacture 1
 */
@GenerateWebScript(
	url="/owsi/batch/replacePropertyValue?property={property}&old={oldValue}&new={newValue}",
	description="Remplace la valeur d'une propriété sur toutes les nodes où elle valait une autre valeur",
	formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT,
	family=OwsiSwaggerWebScript.WS_FAMILY,
	authentication=GenerateWebScriptAuthentication.USER,
	swaggerParameters={
		@SwaggerParameter(name="property", description = "La propriété où chercher la valeur"),
		@SwaggerParameter(name="old", description = "L'ancienne valeur à chercher"),
		@SwaggerParameter(name="new", description = "La nouvelle valeur à affecter"),
	})
public class ReplacePropertyValueWebScript extends AbstractWebScript {
	
	@Autowired private NodeSearchModelRepositoryService nodeSearchModelRepositoryService;
	@Autowired private NodeService nodeService;
	@Autowired
	@Qualifier("NamespaceService")
	private NamespacePrefixResolver prefixResolver;
	@Autowired private ConversionService conversionService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setContentType("text/plain");

		QName property = QName.resolveToQName(prefixResolver, req.getParameter("property"));
		NameReference propertyNameReference = conversionService.get(property);
		TextPropertyModel propertyModel = new TextPropertyModel(new ContainerModel(propertyNameReference), propertyNameReference);
		String oldValue = req.getParameter("old");
		String newValue = req.getParameter("new");
		
		BatchSearchQueryBuilder builder = new BatchSearchQueryBuilder();
		builder.configurationName(
				"owsi.replacePropertyValue", 
				"owsi.replacePropertyValue." + property.toPrefixString());
		builder.restriction(new RestrictionBuilder()
				.eq(propertyModel, oldValue).of());
		builder.consumer(new Consumer<NodeRef>() {
			@Override
			public void accept(NodeRef nodeRef) {
				nodeService.setProperty(nodeRef, property, newValue);
			}
		});
		int total = nodeSearchModelRepositoryService.searchBatch(builder);
		
		res.getWriter().append(total + " node(s) traitée(s)");
	}
}

