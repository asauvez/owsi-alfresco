package fr.openwide.alfresco.repo.wsgenerator.processor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.Filer;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileObject;

import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2ClassAssociation;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.dictionary.M2Type;
import org.apache.commons.lang3.StringUtils;
import org.jibx.runtime.impl.IXMLReaderFactory;

public class JavaModelGenerator {

	public void generate(String modelPath, Filer filer) {
		try {
			File moduleFolder = getModuleFolder(filer);
			File resourceFolder = new File(moduleFolder, "src/main/resources/");
			File modelFile = new File(resourceFolder, modelPath);
			if (! modelFile.exists() && modelPath.startsWith("alfresco/module/")) {
				File configFolder = new File(moduleFolder, "src/main/config");
				String modelPathConfig = StringUtils.substringAfter(modelPath.substring("src/main/resources/".length()), "/");
				File modelFileConfig = new File(configFolder, modelPathConfig);
				if (modelFileConfig.exists()) {
					modelFile = modelFileConfig;
				}
			}
			
			M2Model model = loadModel(modelFile);
			for (M2Namespace namespace : model.getNamespaces()) {
				String packageName = getPackageName(namespace.getPrefix());
				String javaRootInterfaceName = StringUtils.capitalize(namespace.getPrefix()) + "Model";
				JavaFileObject javaRootInterface = filer.createSourceFile(packageName + "." + javaRootInterfaceName);
				try (Writer writer = javaRootInterface.openWriter()) {
					writer.append("package ").append(packageName).append(";\n\n")
						.append("import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;\n\n")
						.append("public interface ").append(javaRootInterfaceName).append(" {\n\n")
						.append("	NamespaceReference NAMESPACE = NamespaceReference.create(\"")
							.append(namespace.getPrefix()).append("\", \"").append(namespace.getUri()).append("\");\n\n");
					
					for (M2Aspect aspect : model.getAspects()) {
						if (isSameNameSpace(namespace.getPrefix() + ":", aspect.getName())) {
							manageClass(writer, javaRootInterfaceName, aspect, filer);
						}
					}
					for (M2Type type : model.getTypes()) {
						if (isSameNameSpace(namespace.getPrefix() + ":", type.getName())) {
							manageClass(writer, javaRootInterfaceName, type, filer);
						}
					}
					writer.append("}\n");
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	private M2Model loadModel(File modelFile) throws IOException {
		ClassLoader oldContextClassLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream input = new BufferedInputStream(new FileInputStream(modelFile))) {
			Thread.currentThread().setContextClassLoader(IXMLReaderFactory.class.getClassLoader());
			return M2Model.createModel(input);
		} finally {
			Thread.currentThread().setContextClassLoader(oldContextClassLoader);
		}
	}
	
	private File getModuleFolder(Filer filer) throws IOException {
		JavaFileObject fakeObject = filer.createSourceFile("OwsiFakeObject");
		try {
			File fakeFile = new File(fakeObject.toUri().getPath());
			return new File(fakeFile.getParentFile(), "../../..").getCanonicalFile();
		} finally {
			fakeObject.delete();
		}
	}

	private void manageClass(Writer javaRootInterfaceWriter, String javaRootInterfaceName, M2Class m2Class, Filer filer) throws IOException {
		String classPrefix = StringUtils.substringBefore(m2Class.getName(), ":");
		String containerName = StringUtils.substringAfter(m2Class.getName(), ":");
		String className = getClassName(m2Class.getName());
		javaRootInterfaceWriter.append("	").append(className).append(" ").append(containerName).append(" = new ").append(className).append("();\n");
		
		String packageName = getPackageName(classPrefix);
		JavaFileObject classObject = filer.createSourceFile(packageName + "." + className);
		try (Writer writer = classObject.openWriter()) {
			writer.append("package ").append(packageName).append(";\n\n");

			Set<String> classesToImport = new TreeSet<>();
			classesToImport.add("fr.openwide.alfresco.api.core.remote.model.NameReference");
			
			String containerType = (m2Class instanceof M2Aspect) ? "AspectModel" : "TypeModel";
			if (m2Class.getParentName() != null 
				&& classPrefix.equals(StringUtils.substringBefore(m2Class.getParentName(), ":"))) {
				containerType = getClassName(m2Class.getParentName());
			} else {
				classesToImport.add("fr.openwide.alfresco.component.model.node.model." + containerType);
			}
			
			for (M2Property property : m2Class.getProperties()) {
				classesToImport.add("fr.openwide.alfresco.component.model.node.model.property.PropertyModels");
				classesToImport.add("fr.openwide.alfresco.component.model.node.model.property."
						+ (property.isMultiValued() ? "multi.Multi" : "single.")
						+ getType(property.getType()) + "PropertyModel ");
			}
			for (M2ClassAssociation assoc : m2Class.getAssociations()) {
				classesToImport.add("fr.openwide.alfresco.component.model.node.model.association." + getAssocClassName(assoc));
			}
			for (String classToImport : classesToImport) {
				writer.append("import ").append(classToImport).append(";\n");
			}
				
			writer.append("\npublic class ").append(className).append(" extends ")
					.append(containerType).append(" {\n\n")
				.append("	public ").append(className).append("() {\n")
				.append("		super(NameReference.create(").append(javaRootInterfaceName).append(".NAMESPACE, \"").append(containerName).append("\"));\n")
				.append("	}\n\n")
				.append("	protected ").append(className).append("(NameReference nameReference) {\n")
				.append("		super(nameReference);\n")
				.append("	}\n\n");
			
			for (M2Property property : m2Class.getProperties()) {
				manageProperty(writer, javaRootInterfaceName, property);
			}

			for (M2ClassAssociation assoc : m2Class.getAssociations()) {
				manageAssoc(writer, javaRootInterfaceName, assoc);
			}

			for (String aspect : m2Class.getMandatoryAspects()) {
				// On ne gére que les aspects du même modèle pour le moment.
				if (isSameNameSpace(m2Class.getName(), aspect)) {
					manageMandatoryAspect(writer, javaRootInterfaceName, aspect);
				}
			}
			
			writer.append("}\n");
		}
	}
	
	private boolean isSameNameSpace(String name1, String name2) {
		String prefix1 = StringUtils.substringBefore(name1, ":");
		String prefix2 = StringUtils.substringBefore(name2, ":");
		return prefix1.contentEquals(prefix2);
	}
	
	private void manageProperty(Writer writer, String javaRootInterfaceName, M2Property property) throws IOException {
		String name = StringUtils.substringAfter(property.getName(), ":");
		String type = getType(property.getType());
		String multi = property.isMultiValued() ? "Multi" : "";
		writer.append("	public final ").append(multi).append(type).append("PropertyModel ").append(toJavaName(name))
			.append(" = PropertyModels.new").append(multi).append(type).append("(this, ").append(javaRootInterfaceName)
			.append(".NAMESPACE, \"").append(name).append("\");\n");
	}

	private void manageAssoc(Writer writer, String javaRootInterfaceName, M2ClassAssociation assoc) throws IOException {
		String name = StringUtils.substringAfter(assoc.getName(), ":");
		String className = getAssocClassName(assoc);
		
		writer.append("	public final ").append(className).append(" ").append(toJavaName(name))
			.append(" = new ").append(className).append("(NameReference.create(")
			.append(javaRootInterfaceName).append(".NAMESPACE, \"").append(name).append("\"));\n");
	}
	private String getAssocClassName(M2ClassAssociation assoc) {
		return (assoc.isSourceMany() ? "Many" : "One")
			+ "To" + (assoc.isTargetMany() ? "Many" : "One")
			+ "AssociationModel";
	}

	private void manageMandatoryAspect(Writer writer, String javaRootInterfaceName, String aspectName) throws IOException {
		String name = StringUtils.substringAfter(aspectName, ":");
		writer.append("	public final ").append(getClassName(aspectName))
			.append(" ").append(name)
			.append(" = addMandatoryAspect(").append(javaRootInterfaceName)
			.append(".").append(name).append(");\n");
	}

	private String getType(String type) {
		switch (type) {
		case "d:int": return "Integer";
		case "d:datetime": return "DateTime";
		case "d:noderef": return "NodeReference";
		case "d:qname": return "NameReference";
		case "d:category": return "NodeReference";
		case "d:mltext": return "Text";
		}
		return StringUtils.capitalize(StringUtils.substringAfter(type, ":"));
	}
	
	private String getClassName(String containerName) {
		String prefix = StringUtils.substringBefore(containerName, ":");
		String name = StringUtils.substringAfter(containerName, ":");
		return toJavaName(StringUtils.capitalize(prefix) + StringUtils.capitalize(name));
	}
	
	private String toJavaName(String s) {
		if (SourceVersion.isKeyword(s)) {
			s = s + "_";
		}
		return s.replace("-", "_");
	}

	private String getPackageName(String prefix) {
		return "fr.openwide.alfresco." + toJavaName(prefix) + ".model";
	}
}
