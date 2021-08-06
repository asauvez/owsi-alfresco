package fr.openwide.alfresco.repo.dictionary.search.web.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Consumer;

import org.activiti.engine.impl.util.json.JSONObject;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
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
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;

@GenerateWebScript(
	url="/owsi/batch/replacePropertyValue",
	shortName="Remplace la valeur d'une propriété sur toutes les nodes où elle valait une autre valeur",
	description = "http://localhost:8080/alfresco/s/owsi/batch/replacePropertyValue\n" + 
			"		?property=exif:manufacturer&old=OLYMPUS OPTICAL CO.,LTD&new=Manufacture 1",
	formatDefaultEnum=GenerateWebScriptFormatDefault.TEXT,
	family=OwsiSwaggerWebScript.WS_FAMILY,
	authentication=GenerateWebScriptAuthentication.USER,
	swaggerParameters={
		@SwaggerParameter(name="property", description = "La propriété où modifier la valeur", required=true),
		@SwaggerParameter(name="old", description = "L'ancienne valeur à chercher", required=true),
		@SwaggerParameter(name="new", description = "La nouvelle valeur à affecter", required=true),
	})
public class ReplacePropertyValueWebScript extends AbstractWebScript {
	
	@Autowired private NodeSearchModelRepositoryService nodeSearchModelRepositoryService;
	@Autowired private NodeService nodeService;
	@Autowired @Qualifier("NamespaceService") private NamespacePrefixResolver prefixResolver;
	@Autowired private ConversionService conversionService;
	@Autowired private DictionaryService dictionaryService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setContentType("application/json");

		String property = req.getParameter("property");
		String oldValue = req.getParameter("old");
		String newValue = req.getParameter("new");
		
		QName propertyQName = QName.resolveToQName(prefixResolver, property);
		PropertyDefinition propertyDefinition = dictionaryService.getProperty(propertyQName);
		if (propertyDefinition == null) {
			throw new IllegalStateException(property + " is not defined");
		}
		
		BatchSearchQueryBuilder builder = new BatchSearchQueryBuilder()
			.configurationName(
				"owsi.replacePropertyValue", 
				"owsi.replacePropertyValue." + propertyQName.toPrefixString());
		
		// Condition
		NameReference propertyWhereNameReference = conversionService.get(QName.resolveToQName(prefixResolver,property));
		TextPropertyModel propertyWhereModel = new TextPropertyModel(new ContainerModel(propertyWhereNameReference), propertyWhereNameReference);
		builder.restriction(new RestrictionBuilder()
				.eq(propertyWhereModel, oldValue).of());
		
		// Action
		builder.consumer(new Consumer<NodeRef>() {
			@Override
			public void accept(NodeRef nodeRef) {
				if (propertyDefinition.isMultiValued()) {
					@SuppressWarnings("unchecked")
					Collection<String> oldCol = (Collection<String>) nodeService.getProperty(nodeRef, propertyQName);
					ArrayList<String> newCol = new ArrayList<>();
					for (String oldItem : oldCol) {
						newCol.add(Objects.equals(oldValue, oldItem) ? newValue : oldItem);
					}
					nodeService.setProperty(nodeRef, propertyQName, newCol);
				} else {
					nodeService.setProperty(nodeRef, propertyQName, newValue);
				}
			}
		});
		int total = nodeSearchModelRepositoryService.searchBatch(builder);
		
		res.getWriter().append(new JSONObject().put("total", total).toString());
	}
}

