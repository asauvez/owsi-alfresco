package fr.openwide.alfresco.repo.module.classification.model.policy;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.api.core.remote.model.NameReference;
import fr.openwide.alfresco.component.model.node.model.ContainerModel;
import fr.openwide.alfresco.component.model.search.model.restriction.RestrictionBuilder;
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
	
	private Template ftsRootTemplate = null;
	private List<Template> subfoldersTemplates = new ArrayList<>();
	private boolean uniqueName;
	private String folderSeparator = "/";
	private Optional<String> multiFolderSeparator;

	public FreeMarkerClassificationPolicy(Properties globalProperties, ContainerModel model) throws IOException {
		Configuration cfg = new Configuration();

		String policyKey = model.getNameReference().getFullName().replace(':', '_');
		
		String ftsRootPropertyKey = "owsi.classification." + policyKey + ".root.fts";
		String ftsRootTemplatesAsString = globalProperties.getProperty(ftsRootPropertyKey);
		if (StringUtils.isNoneEmpty(ftsRootTemplatesAsString)) {
			ftsRootTemplate = new Template(ftsRootPropertyKey, new StringReader(ftsRootTemplatesAsString), cfg);
		}

		String folderSeparatorKey = "owsi.classification." + policyKey + ".folderSeparator";
		folderSeparator = globalProperties.getProperty(folderSeparatorKey, "/");

		String multiFolderSeparatorKey = "owsi.classification." + policyKey + ".multiFolderSeparator";
		multiFolderSeparator = Optional.ofNullable(globalProperties.getProperty(multiFolderSeparatorKey, null));

		String subfoldersPropertyKey = "owsi.classification." + policyKey + ".subfolders";
		String subfoldersTemplatesAsString = globalProperties.getProperty(subfoldersPropertyKey);
		if (subfoldersTemplatesAsString == null) {
			throw new IllegalStateException("Ne trouve pas la propriété " + subfoldersPropertyKey);
		}
		
		for (String templateAsString : subfoldersTemplatesAsString.split(folderSeparator)) {
			if (! templateAsString.isEmpty()) {
				subfoldersTemplates.add(new Template(subfoldersPropertyKey, new StringReader(templateAsString), cfg));
			}
		}
		
		String uniqueNamePropertyKey = "owsi.classification." + policyKey + ".uniquename";
		uniqueName = Boolean.valueOf(globalProperties.getProperty(uniqueNamePropertyKey, "false"));
	}
	
	@Override
	public void classify(ClassificationBuilder builder, ContainerModel model, ClassificationEvent event) {
		Map<NameReference, Serializable> properties = builder.getNodeModelService().getProperties(builder.getNodeRef());
		
		Map<String, Object> dataModel = new HashMap<>();
		for (Entry<NameReference, Serializable> entry : properties.entrySet()) {
			dataModel.put(entry.getKey().getFullName().replace(':', '_'), entry.getValue());
		}
		
		ClassificationWithRootBuilder rootBuilder;
		if (ftsRootTemplate != null) {
			String ftsRoot = processTemplate(ftsRootTemplate, dataModel);
			Optional<ClassificationWithRootBuilder> optional = builder.rootFolder(new RestrictionBuilder()
					.custom(ftsRoot).of());
			if (optional.isPresent()) {
				rootBuilder = optional.get();
			} else {
				throw new IllegalStateException(ftsRoot + " ne renvoie rien.");
			}
		} else {
			rootBuilder = builder.rootCompanyHome();
		}
		
		
		for (Template template : subfoldersTemplates) {
			String folderName = processTemplate(template, dataModel);
			if (multiFolderSeparator.isPresent()) {
				Set<String> folderNames = new TreeSet<String>(Arrays.asList(folderName.split(multiFolderSeparator.get())));
				folderNames.remove("");
				rootBuilder.subFolders(folderNames);
			} else {
				rootBuilder.subFolder(folderName);
			}
		}
		
		if (uniqueName) {
			rootBuilder.uniqueName();
		}
		rootBuilder.moveFirstAndCreateSecondaryParents();
	}
	
	private String processTemplate(Template template, Map<String, Object> dataModel) {
		StringWriter out = new StringWriter();
		try {
			template.process(dataModel, out);
		} catch (TemplateException | IOException e) {
			throw new IllegalStateException(template.getName(), e);
		}
		return out.toString();
	}
}
