package fr.openwide.alfresco.repo.core.constraintvalues.web.script;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.TypeModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
import fr.openwide.alfresco.repo.core.configurationlogger.AlfrescoGlobalProperties;
import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.core.swagger.web.script.SwaggerWebScript;
import fr.openwide.alfresco.repo.dictionary.search.service.NodeSearchModelRepositoryService;
import fr.openwide.alfresco.repo.remote.conversion.service.ConversionService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;


/**
 * Renvoie les valeurs possibles pour une DataList.
 * 
 * http://localhost:8080/alfresco/s/owsi/datalist-values?type=dl:todoList
 * 
 * Il faut rajouter le type dans la liste des types de DataList autorisés dans alfresco-global.properties :
 * owsi.datalist-values.authorizedTypes=dl:todoList,va:1LevelValueAssistanceListItem,va:2LevelCascadingValueAssistanceListItem
 */
@GenerateWebScript(
		url="/owsi/datalist-values",
		description="Liste les valeurs possibles d'une DataList",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		authentication=GenerateWebScriptAuthentication.NONE,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		formatDefaultEnum=GenerateWebScriptFormatDefault.JSON)
public class DataListValuesWebScript extends SwaggerWebScript implements InitializingBean {

	private ObjectMapper mapper = new ObjectMapper();
	
	private Set<String> authorizedTypes;
	
	@Autowired private NodeSearchModelRepositoryService nodeSearchModelRepositoryService;
	@Autowired private ConversionService conversionService;
	
	@Autowired AlfrescoGlobalProperties globalProperties;
	@Autowired private NodeService nodeService;
	@Autowired private DictionaryService dictionaryService;
	@Autowired @Qualifier("NamespaceService")
	private NamespacePrefixResolver prefixResolver;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		String authorizedTypesName = globalProperties.getProperty("owsi.datalist-values.authorizedTypes", 
				"va:1LevelValueAssistanceListItem,va:2LevelCascadingValueAssistanceListItem");
		this.authorizedTypes = new HashSet<>(Arrays.asList(authorizedTypesName.split(",")));
	}
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String dataListTypeName = req.getParameter("type");
		if (dataListTypeName == null) {
			throw new IllegalArgumentException("Expected argument 'type'");
		}
		if (! authorizedTypes.contains(dataListTypeName)) {
			throw new IllegalArgumentException("You should add this type to configuration 'owsi.datalist-values.authorizedTypes'");
		}
		
		List<Map<String, Serializable>> options = AuthenticationUtil.runAsSystem(new AuthenticationUtil.RunAsWork<List<Map<String, Serializable>>>() {
			@Override
			public List<Map<String, Serializable>> doWork() throws Exception {
				return getListValues(dataListTypeName);
			}
		});
		
		res.setContentType("application/json");
		mapper.writeValue(res.getOutputStream(), options);
	}
	
	private List<Map<String, Serializable>> getListValues(String dataListTypeName) {
		TypeDefinition typeDef = dictionaryService.getType(QName.createQName(dataListTypeName, prefixResolver));
		if (typeDef == null) {
			throw new IllegalArgumentException("Unknow type '" + dataListTypeName + "'");
		}
//		if (! DataListModel.TYPE_DATALIST_ITEM.equals(typeDef.getParentName())) {
//			throw new IllegalArgumentException("Expected subtype of '" + DataListModel.TYPE_DATALIST_ITEM.toPrefixString(prefixResolver) + "' but got '" + dataListTypeName + "'");
//		}
		NameReference nameReference = conversionService.get(typeDef.getName());
		
		List<Map<String, Serializable>> options = new ArrayList<>();
		List<NodeRef> items = nodeSearchModelRepositoryService.searchReference(new RestrictionBuilder()
				.isType(new TypeModel(nameReference)).of());
		
		for (NodeRef item : items) {
			Map<String, Serializable> map = new HashMap<>();
			map.put("nodeRef", item.toString());
			
			Map<QName, Serializable> properties = nodeService.getProperties(item);
			for (Entry<QName, Serializable> entry : properties.entrySet()) {
				QName property = entry.getKey();
				if (   ! NamespaceService.CONTENT_MODEL_1_0_URI.equals(property.getNamespaceURI())
					&& ! NamespaceService.SYSTEM_MODEL_1_0_URI.equals(property.getNamespaceURI())) {
					
					map.put(property.toPrefixString(prefixResolver).replace(':', '_'), entry.getValue());
				}
			}
			options.add(map);
		}
		return options;
	}
}
