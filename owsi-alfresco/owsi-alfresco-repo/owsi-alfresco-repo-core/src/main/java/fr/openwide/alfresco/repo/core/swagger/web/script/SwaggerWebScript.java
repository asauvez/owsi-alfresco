package fr.openwide.alfresco.repo.core.swagger.web.script;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.repo.admin.SysAdminParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.customProperties.ValidationSchemaFactoryWrapper;

import fr.openwide.alfresco.repo.core.swagger.model.SwaggerParameterModel;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerResponseModel;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerRoot;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerSchema;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerWS;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerResponse;

public class SwaggerWebScript extends AbstractWebScript {

	@Autowired private ApplicationContext applicationContext;
	@Autowired private SysAdminParams sysAdminParams;

	private ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
	private JsonSchemaGenerator schemaGen = new JsonSchemaGenerator(objectMapper, new ValidationSchemaFactoryWrapper());
	

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setHeader("Access-Control-Allow-Origin", "*");

		res.setContentType("application/x-yaml");
		SwaggerRoot root = getRoot();
		String yaml = objectMapper.writeValueAsString(root);
		yaml = yaml.substring("---\n".length());
		res.getWriter().append(yaml).flush();
	}
	
	/** Protected pour permettre aux sous classes de modifier ce qui est généré */
	protected SwaggerRoot getRoot() throws JsonMappingException {
		Map<String, WebScript> webscripts = applicationContext.getBeansOfType(WebScript.class);
		
		SwaggerRoot root = new SwaggerRoot();
		root.host = sysAdminParams.getAlfrescoHost() + ":" + sysAdminParams.getAlfrescoPort();
		root.basePath = "/" + sysAdminParams.getAlfrescoContext() + "/s";
		root.schemes = new String[] { sysAdminParams.getAlfrescoProtocol() };
		
		root.title = getRootTitle();
		root.description = getRootDescription();
		root.version = getRootVersion();
		
		for (WebScript webscript : webscripts.values()) {
			GenerateWebScript annotation = webscript.getClass().getAnnotation(GenerateWebScript.class);
			if (isWebScriptFiltered(annotation, webscript)) {
				for (String url : annotation.url()) {
					Map<String, SwaggerWS> methods = root.paths.get(url);
					if (methods == null) {
						root.paths.put(url,  methods = new TreeMap<>());
					}
					SwaggerWS ws = new SwaggerWS();
					ws.summary = annotation.shortName();
					ws.description = annotation.description();
					//ws.operationId = webscript.getClass().getName();
					ws.tags = new String[] { annotation.family() };
					
					switch (annotation.formatDefault()) {
					case "text": ws.produces.add("text/plain"); break;
					case "json": ws.produces.add("application/json"); break;
					case "html": ws.produces.add("text/html"); break;
					}
					
					for (SwaggerParameter param : annotation.swaggerParameters()) {
						SwaggerParameterModel model = new SwaggerParameterModel();
						model.name = param.name();
						model.description = param.description();
						model.in = param.in().name().toLowerCase();
						model.required = param.required();
						model.type = param.type();
						if (param.schema() != Void.class) {
							model.type = "object";
							model.schema = new SwaggerSchema(schemaGen.generateSchema(param.schema()));
						}
						
						ws.parameters.add(model);
					}
					for (SwaggerResponse resp : annotation.swaggerResponses()) {
						SwaggerResponseModel model = new SwaggerResponseModel();
						model.description = resp.description();
						if (resp.schema() != Void.class) {
							model.schema = new SwaggerSchema(schemaGen.generateSchema(resp.schema()));
						}
						ws.responses.put(Integer.toString(resp.statusCode()), model);
					}
					
					methods.put(annotation.method().name().toLowerCase(), ws);
				}
			}
		}
		return root;
	}

	public String getRootTitle() { return "API"; };
	public String getRootDescription() { return ""; };
	public String getRootVersion() { return "1.0"; };
	
	public boolean isWebScriptFiltered(GenerateWebScript annotation, @SuppressWarnings("unused") WebScript webscript) {
		return annotation != null && annotation.visibleInSwagger();
	}
}
