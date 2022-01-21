package fr.openwide.alfresco.repo.wsgenerator.processor;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Writer;

import javax.annotation.processing.Filer;
import javax.lang.model.SourceVersion;
import javax.tools.JavaFileObject;

import org.alfresco.repo.dictionary.M2Aspect;
import org.alfresco.repo.dictionary.M2Class;
import org.alfresco.repo.dictionary.M2Model;
import org.alfresco.repo.dictionary.M2Namespace;
import org.alfresco.repo.dictionary.M2Property;
import org.alfresco.repo.dictionary.M2Type;
import org.apache.commons.lang3.StringUtils;

public class JavaModelGenerator {

	public static void main(String[] args) throws IOException {
		M2Model model = M2Model.createModel(new FileInputStream("/home/adrsau/Projets/AlfrescoQueryForm/workspace/alfresco-tools/owsi-alfresco/owsi-alfresco-repo/owsi-alfresco-repo-module/src/main/config/model/owsiModel.xml"));
		System.out.println(model.getName());
	}
	
	public void generate(String modelPath, Filer filer) {
		try {
			JavaFileObject fakeObject = filer.createSourceFile("OwsiFakeObject");
			File fakeFile = new File(fakeObject.toUri().getPath());
			File moduleFolder = new File(fakeFile.getParentFile(), "../../..").getCanonicalFile();
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
			fakeObject.delete();
			
			try (InputStream input = new BufferedInputStream(new FileInputStream(modelFile))) {
				M2Model model = M2Model.createModel(input);
				
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
							manageClass(writer, javaRootInterfaceName, aspect, filer);
						}
						for (M2Type type : model.getTypes()) {
							manageClass(writer, javaRootInterfaceName, type, filer);
						}
						writer.append("}\n");
					}
				}
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}

	private void manageClass(Writer javaRootInterfaceWriter, String javaRootInterfaceName, M2Class m2Class, Filer filer) throws IOException {
		String prefix = StringUtils.substringBefore(m2Class.getName(), ":");
		String name = StringUtils.substringAfter(m2Class.getName(), ":");
		String className = toJavaName(StringUtils.capitalize(prefix) + StringUtils.capitalize(name));
		javaRootInterfaceWriter.append("	").append(className).append(" ").append(name).append(" = new ").append(className).append("();\n");
		
		String packageName = getPackageName(prefix);
		JavaFileObject classObject = filer.createSourceFile(packageName + "." + className);
		try (Writer writer = classObject.openWriter()) {
			writer.append("package ").append(packageName).append(";\n\n")
				.append("import fr.openwide.alfresco.api.core.remote.model.NamespaceReference;\n")
				.append("import fr.openwide.alfresco.component.model.node.model.*;\n\n")
				.append("public class ").append(className).append(" extends ")
					.append((m2Class instanceof M2Aspect) ? "AspectModel" : "TypeModel").append(" {\n\n")
				.append("	public ").append(className).append("() {\n")
				.append("		super(NameReference.create(").append(javaRootInterfaceName).append(".NAMESPACE, \"").append(m2Class.getName()).append("\"));\n")
				.append("	}\n\n");
			
			for (M2Property property : m2Class.getProperties()) {
				manageProperty(writer, javaRootInterfaceName, property);
			}
			
			writer.append("}\n");
		}
	}
	
	private void manageProperty(Writer writer, String javaRootInterfaceName, M2Property property) throws IOException {
		String name = StringUtils.substringAfter(property.getName(), ":");
		String type = getType(property.getType());
		String multi = property.isMultiValued() ? "Multi" : "";
		writer.append("	public final ").append(multi).append(type).append("PropertyModel ").append(toJavaName(name))
			.append(" = PropertyModels.new").append(multi).append(type).append("(this, ").append(javaRootInterfaceName)
			.append(".NAMESPACE, \"").append(name).append("\");");
	}
	
	private String getType(String type) {
		switch (type) {
		case "d:int": return "Integer";
		case "d:datetime": return "DateTime";
		case "d:noderef": return "NodeRef";
		}
		return StringUtils.capitalize(StringUtils.substringAfter(type, ":"));
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
