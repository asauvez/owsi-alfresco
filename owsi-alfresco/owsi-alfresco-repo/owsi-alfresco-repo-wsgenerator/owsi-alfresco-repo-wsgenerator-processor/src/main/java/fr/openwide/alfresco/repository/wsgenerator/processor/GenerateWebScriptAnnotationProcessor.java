package fr.openwide.alfresco.repository.wsgenerator.processor;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.api.core.remote.model.endpoint.RemoteEndpoint.RemoteEndpointMethod;
import fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript;

@SupportedSourceVersion(SourceVersion.RELEASE_7)
@SupportedAnnotationTypes("fr.openwide.alfresco.repository.wsgenerator.annotation.GenerateWebScript")
public class GenerateWebScriptAnnotationProcessor extends AbstractProcessor {

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> annotatedClasses = roundEnv.getElementsAnnotatedWith(GenerateWebScript.class);
		for (Element annotatedClassElement : annotatedClasses) {
			GenerateWebScript classAnnotation = annotatedClassElement.getAnnotation(GenerateWebScript.class);
			Name className = ((TypeElement) annotatedClassElement).getQualifiedName();
			
			String url = classAnnotation.url();
			String folderName = StringUtils.substringBeforeLast(url, "/");
			String wsName = StringUtils.substringAfterLast(url, "/").replace(".", "-");

			RemoteEndpointMethod method = classAnnotation.method();
			
			String family = classAnnotation.family();
			if (family.isEmpty() && url.split("/").length > 1) {
				family = url.split("/")[1];
			}

			Filer filer = processingEnv.getFiler();
			try {
				FileObject descXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
						"alfresco.extension.templates.webscripts" + folderName.replace("/", "."), 
						wsName + "." + method.name().toLowerCase() + ".desc.xml");
				try (PrintWriter out = new PrintWriter(descXml.openWriter())) {
					out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
					out.println("<webscript>");
					out.println("	<shortname>" + ((classAnnotation.shortName() != null) ? classAnnotation.shortName() : wsName) + "</shortname>");
					out.println("	<description>" + ((classAnnotation.description() != null) ? classAnnotation.description() : className) + "</description>");
					out.println("	<url>" + url + "</url>");
					out.println("	<format default=\"" + classAnnotation.formatDefault() + "\">" + classAnnotation.format() + "</format>");
					out.println("	<authentication>" + classAnnotation.authentication().name().toLowerCase() + "</authentication>");
					out.println("	<transaction allow=\"" + classAnnotation.transactionAllow().name().toLowerCase()+ "\">" + classAnnotation.transaction().name().toLowerCase() + "</transaction>");
					out.println("	<family>" + family + "</family>");
					out.println("</webscript>");

					out.flush();
				}
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + descXml.toUri() + " a été généré");

				FileObject springContextXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
						"alfresco.owsi", 
						"wsgenerator" + folderName.replace("/", "-") + "-" + wsName + "-" + method.name().toLowerCase() + "-context.xml");
				try (PrintWriter out = new PrintWriter(springContextXml.openWriter())) {
					out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
					out.println("<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN' 'http://www.springframework.org/dtd/spring-beans.dtd'>");
					out.println("<beans>");
					out.println("	<bean id=\"webscript" + folderName.replace("/", ".") + "." + wsName + "." + method.name().toLowerCase() 
							+ "\" parent=\"" + classAnnotation.beanParent() + "\"");
					out.println("		class=\"" + className + "\" />");
					out.println("</beans>");

					out.flush();
				}
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + springContextXml.toUri() + " a été généré");
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		return true;
	}


}
