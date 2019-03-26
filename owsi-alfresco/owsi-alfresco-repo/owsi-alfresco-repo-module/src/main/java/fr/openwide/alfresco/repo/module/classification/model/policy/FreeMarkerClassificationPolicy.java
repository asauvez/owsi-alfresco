package fr.openwide.alfresco.repo.module.classification.model.policy;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.repo.module.classification.model.ClassificationEvent;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationBuilder;
import fr.openwide.alfresco.repo.module.classification.model.builder.ClassificationWithRootBuilder;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;


/**
 * Permet de faire une classification juste en définissant les propriétés suivantes :
 * 
 *  owsi.classification.freemarker.models=exif:exif
*   owsi.classification.exif_exif.subfolders=/Demo/exif/${cm_created?string.yyyy}/${exif_pixelXDimension}/${exif_pixelYDimension}/
 *
 */
public class FreeMarkerClassificationPolicy implements ClassificationPolicy<ContainerModel> {
	
	private List<Template> templates = new ArrayList<>();

	public FreeMarkerClassificationPolicy(Properties globalProperties, ContainerModel model) throws IOException {
		String propertyKey = "owsi.classification." + model.getNameReference().getFullName().replace(':', '_') + ".subfolders";
		String templatesAsString = globalProperties.getProperty(propertyKey);
		if (templatesAsString == null) {
			throw new IllegalStateException("Ne trouve pas la propriété " + propertyKey);
		}
		
		Configuration cfg = new Configuration();
		for (String templateAsString : templatesAsString.split("/")) {
			if (! templateAsString.isEmpty()) {
				templates.add(new Template(templateAsString, new StringReader(templateAsString), cfg));
			}
		}
	}
	
	@Override
	public void classify(ClassificationBuilder builder, ContainerModel model, ClassificationEvent event) {
		Map<NameReference, Serializable> properties = builder.getNodeModelService().getProperties(builder.getNodeRef());
		
		Map<String, Object> dataModel = new HashMap<>();
		for (Entry<NameReference, Serializable> entry : properties.entrySet()) {
			dataModel.put(entry.getKey().getFullName().replace(':', '_'), entry.getValue());
		}
		
		ClassificationWithRootBuilder rootBuilder = builder.rootCompanyHome();
		for (Template template : templates) {
			StringWriter out = new StringWriter();
			try {
				template.process(dataModel, out);
			} catch (TemplateException | IOException e) {
				throw new IllegalStateException(model.getNameReference() + " " + template.getName(), e);
			}
			String folderName = out.toString();
			rootBuilder.subFolder(folderName);
		}
		
		rootBuilder.moveNode();
	}
}
