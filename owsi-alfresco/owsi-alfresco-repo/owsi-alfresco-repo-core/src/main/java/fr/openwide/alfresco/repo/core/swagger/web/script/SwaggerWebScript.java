package fr.openwide.alfresco.repo.core.swagger.web.script;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.alfresco.repo.admin.SysAdminParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jsonSchema.JsonSchemaGenerator;
import com.fasterxml.jackson.module.jsonSchema.factories.SchemaFactoryWrapper;
import com.fasterxml.jackson.module.jsonSchema.factories.VisitorContext;

import fr.openwide.alfresco.repo.core.swagger.model.SwaggerInfo;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerParameterModel;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerResponseModel;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerRoot;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerSchema;
import fr.openwide.alfresco.repo.core.swagger.model.SwaggerWS;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.WebScriptMethod;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter.SwaggerParameterIn;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerResponse;

public abstract class SwaggerWebScript extends AbstractWebScript {

	@Autowired private ApplicationContext applicationContext;
	@Autowired private SysAdminParams sysAdminParams;

	private ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());

	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		res.setHeader("Access-Control-Allow-Origin", "*");

		//res.setContentType("application/x-yaml");
		res.setContentType("text/plain");
		res.setContentEncoding("UTF-8");
		SwaggerRoot root = getRoot();
		String yaml = objectMapper.writeValueAsString(root);
		yaml = yaml.substring("---\n".length());
		res.getOutputStream().write(yaml.getBytes("UTF-8"));
	}
	
	/** Protected pour permettre aux sous classes de modifier ce qui est généré */
	protected SwaggerRoot getRoot() throws JsonMappingException {
		Map<String, WebScript> webscripts = applicationContext.getBeansOfType(WebScript.class);
		
		SwaggerRoot root = new SwaggerRoot();
		root.host = sysAdminParams.getAlfrescoHost() + ":" + sysAdminParams.getAlfrescoPort();
		root.basePath = "/" + sysAdminParams.getAlfrescoContext() + "/s";
		root.schemes = new String[] { sysAdminParams.getAlfrescoProtocol() };
		
		SwaggerInfo infos = new SwaggerInfo();
		infos.title = getRootTitle();
		infos.description = getRootDescription();
		infos.version = getRootVersion();
		root.info = infos;
		
		Map<String, JavaType> seenSchemas = new TreeMap<>();
		SchemaFactoryWrapper visitor = new SchemaFactoryWrapper();
		VisitorContext visitorContext = new VisitorContext() {
			@Override
			public String addSeenSchemaUri(JavaType aSeenSchema) {
				String uri = super.addSeenSchemaUri(aSeenSchema);
				seenSchemas.put(uri, aSeenSchema);
				return uri;
			}
		};
		visitor.setVisitorContext(visitorContext);
		JsonSchemaGenerator schemaGenerator = new JsonSchemaGenerator(objectMapper, visitor);
		
		for (WebScript webscript : webscripts.values()) {
			GenerateWebScript annotation = webscript.getClass().getAnnotation(GenerateWebScript.class);
			if (isWebScriptFiltered(annotation, webscript)) {
				for (WebScriptMethod method : annotation.method()) {
					for (String url : annotation.url()) {
						Map<String, SwaggerWS> methods = root.paths.get(url);
						if (methods == null) {
							root.paths.put(url,  methods = new TreeMap<>());
						}
						SwaggerWS ws = new SwaggerWS();
						ws.summary = annotation.shortName();
						ws.description = annotation.description();
						ws.operationId = annotation.wsName();
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
							
							if (param.schema() != Void.class) {
								model.schema = new SwaggerSchema(schemaGenerator.generateSchema(param.schema()));
							}else {
								if (param.in() == SwaggerParameterIn.BODY) {
									model.schema = new SwaggerSchema(param.type(), "binary");
								} else {
									model.type = param.type();
								}
							}
							
							ws.parameters.add(model);
						}
						for (SwaggerResponse resp : annotation.swaggerResponses()) {
							SwaggerResponseModel model = new SwaggerResponseModel();
							model.description = resp.description();
							if (resp.schema() != Void.class) {
								model.schema = new SwaggerSchema(schemaGenerator.generateSchema(resp.schema()));
							}
							ws.responses.put(Integer.toString(resp.statusCode()), model);
						}
						
						methods.put(method.name().toLowerCase(), ws);
					}
				}
			}
		}
		
		for (Entry<String, JavaType> entry : seenSchemas.entrySet()) {
			root.definitions.put(entry.getKey(), new SwaggerSchema(schemaGenerator.generateSchema(entry.getValue())));
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
