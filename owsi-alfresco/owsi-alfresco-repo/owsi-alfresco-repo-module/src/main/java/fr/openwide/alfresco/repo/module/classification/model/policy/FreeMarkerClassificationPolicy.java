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
 * owsi.classification.freemarker.models=exif:exif
 * owsi.classification.exif_exif.subfolders=/Sites/DAM/documentLibrary/photos/${cm_created?string.yyyy}/${exif_pixelXDimension!"inconnu"}/${exif_pixelYDimension!"inconnu"}/
 * owsi.classification.exif_exif.name=product_image.${cm_name?keep_after_last(".")}
 * owsi.classification.exif_exif.uniquename=true
 * 
 * Classement dans plusieurs dossiers
 * owsi.classification.xxx_bonLivraison.subfolders=/Sites/XXX/documentLibrary/bdl/<#list xxx_codeFournisseur as code>@Fournisseur ${code}</#list>/
 * owsi.classification.xxx_bonLivraison.multiFolderSeparator=@
 */
public class FreeMarkerClassificationPolicy implements ClassificationPolicy<ContainerModel> {
	
	private Optional<Template> ftsRootTemplate = Optional.empty();
	private List<Template> subfoldersTemplates = new ArrayList<>();
	private Optional<Template> newNameTemplate = Optional.empty();
	private boolean uniqueName;
	private Optional<String> multiFolderSeparator;

	public FreeMarkerClassificationPolicy(Properties globalProperties, ContainerModel model) throws IOException {
		Configuration cfg = new Configuration();

		String policyKey = model.getNameReference().getFullName().replace(':', '_');
		
		String ftsRootPropertyKey = "owsi.classification." + policyKey + ".root.fts";
		String ftsRootTemplatesAsString = globalProperties.getProperty(ftsRootPropertyKey);
		if (StringUtils.isNoneEmpty(ftsRootTemplatesAsString)) {
			ftsRootTemplate = Optional.of(new Template(ftsRootPropertyKey, new StringReader(ftsRootTemplatesAsString), cfg));
		}

		String multiFolderSeparatorKey = "owsi.classification." + policyKey + ".multiFolderSeparator";
		multiFolderSeparator = Optional.ofNullable(globalProperties.getProperty(multiFolderSeparatorKey, null));

		String subfoldersPropertyKey = "owsi.classification." + policyKey + ".subfolders";
		String subfoldersTemplatesAsString = globalProperties.getProperty(subfoldersPropertyKey);
		if (subfoldersTemplatesAsString != null) {
			for (String templateAsString : splitIgnoreBracket(subfoldersTemplatesAsString)) {
				if (! templateAsString.isEmpty()) {
					subfoldersTemplates.add(new Template(subfoldersPropertyKey, new StringReader(templateAsString), cfg));
				}
			}
		}

		String newNamePropertyKey = "owsi.classification." + policyKey + ".name";
		String newNameAsString = globalProperties.getProperty(newNamePropertyKey);
		if (newNameAsString != null) {
			newNameTemplate = Optional.of(new Template(newNamePropertyKey, new StringReader(newNameAsString), cfg));
		}
		
		String uniqueNamePropertyKey = "owsi.classification." + policyKey + ".uniquename";
		uniqueName = Boolean.valueOf(globalProperties.getProperty(uniqueNamePropertyKey, "false"));
	}
	
	private static List<String> splitIgnoreBracket(String input) {
		int nParens = 0;
		int start = 0;
		List<String> result = new ArrayList<>();
		for (int i = 0; i < input.length(); i++) {
			switch (input.charAt(i)) {
			case '/':
				if (nParens == 0) {
					result.add(input.substring(start, i));
					start = i + 1;
				}
				break;
			case '{':
			case '<':
				nParens++;
				break;
			case '}':
			case '>':
				nParens--;
				if (nParens < 0)
					throw new IllegalArgumentException("Unbalanced bracket at offset #" + i);
				break;
			}
		}
		if (nParens > 0)
			throw new IllegalArgumentException("Missing closing bracket");
		result.add(input.substring(start));
		return result;
	}

	@Override
	public void classify(ClassificationBuilder builder, ContainerModel model, ClassificationEvent event) {
		Map<NameReference, Serializable> properties = builder.getNodeModelService().getProperties(builder.getNodeRef());
		
		Map<String, Object> dataModel = new HashMap<>();
		for (Entry<NameReference, Serializable> entry : properties.entrySet()) {
			dataModel.put(entry.getKey().getFullName().replace(':', '_'), entry.getValue());
		}
		
		ClassificationWithRootBuilder rootBuilder;
		if (ftsRootTemplate.isPresent()) {
			String ftsRoot = processTemplate(ftsRootTemplate.get(), dataModel);
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
		
		if (newNameTemplate.isPresent()) {
			String newName = processTemplate(newNameTemplate.get(), dataModel);
			rootBuilder.name(newName);
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
