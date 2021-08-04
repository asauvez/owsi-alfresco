package fr.openwide.alfresco.repo.core.constraintvalues.web.script;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.repo.dictionary.constraint.ListOfValuesConstraint;
import org.alfresco.repo.i18n.MessageService;
import org.alfresco.service.cmr.dictionary.Constraint;
import org.alfresco.service.cmr.dictionary.ConstraintDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.namespace.NamespacePrefixResolver;
import org.alfresco.service.namespace.QName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

import com.fasterxml.jackson.databind.ObjectMapper;

import fr.openwide.alfresco.repo.core.swagger.web.script.OwsiSwaggerWebScript;
import fr.openwide.alfresco.repo.core.swagger.web.script.SwaggerWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.SwaggerParameter;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptAuthentication;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptFormatDefault;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptTransactionAllow;

/**
 * Renvoie les valeurs possibles pour une propriété, selon ses contraintes dans le modèles..
 * 
 * http://localhost:8080/alfresco/s/owsi/constraint-values?property=download:status
 */
@GenerateWebScript(
		url="/owsi/constraint-values",
		shortName="Liste les valeurs possibles pour une propriété",
		transactionAllow=GenerateWebScriptTransactionAllow.READONLY,
		authentication=GenerateWebScriptAuthentication.NONE,
		family=OwsiSwaggerWebScript.WS_FAMILY,
		formatDefaultEnum=GenerateWebScriptFormatDefault.JSON,
		swaggerParameters={
			@SwaggerParameter(name="property", description = "Le property où s'applique les contraintes", required=true),
		})
public class ConstraintValuesWebScript extends SwaggerWebScript {

	private ObjectMapper mapper = new ObjectMapper();
	
	@Autowired 
	private DictionaryService dictionaryService;
	@Autowired
	@Qualifier("NamespaceService")
	private NamespacePrefixResolver prefixResolver;
	@Autowired MessageService messageService;
	
	@Override
	public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException {
		String propertyName = req.getParameter("property");
		if (propertyName == null) {
			throw new IllegalArgumentException("Expected argument 'property'");
		}
		List<CardViewSelectItemOption> options = getModelListValues(propertyName);
		
		res.setContentType("application/json");
		mapper.writeValue(res.getOutputStream(), options);
	}
	
	private List<CardViewSelectItemOption> getModelListValues(String propertyName) {
		PropertyDefinition propertyDef = dictionaryService.getProperty(QName.createQName(propertyName, prefixResolver));
		if (propertyDef == null) {
			throw new IllegalArgumentException("Unknow property '" + propertyName + "'");
		}
		
		List<CardViewSelectItemOption> options = new ArrayList<>();
		
		for (ConstraintDefinition constraintDef : propertyDef.getConstraints()) {
			Constraint constraint = constraintDef .getConstraint();
			if (constraint instanceof ListOfValuesConstraint) {
				ListOfValuesConstraint lov = (ListOfValuesConstraint) constraint;
				for (String value : lov.getAllowedValues()) {
					String label = messageService.getMessage(value);
					options.add(new CardViewSelectItemOption(value, label));
				}
			}
		}
		
		return options;
	}

	public static class CardViewSelectItemOption {
		private String key;
		private String label;
		public CardViewSelectItemOption(String key, String label) {
			this.key = key;
			this.label = label;
		}
		public String getKey() { return key; }
		public String getLabel() { return label; }
	}
}
