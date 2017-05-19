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

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

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
			String wsFolder = (! classAnnotation.wsFolder().isEmpty()) ? classAnnotation.wsFolder() : StringUtils.substringBeforeLast(url, "/");
			String wsName = (! classAnnotation.wsName().isEmpty()) ? classAnnotation.wsName() : StringUtils.substringBefore(StringUtils.substringAfterLast(url, "/"), "?").replace(".", "-");
			String method = classAnnotation.method().name().toLowerCase();

			String shortName = (! classAnnotation.shortName().isEmpty()) ? StringEscapeUtils.escapeXml11(classAnnotation.shortName()) : wsName;
			CharSequence description = (! classAnnotation.description().isEmpty()) ? StringEscapeUtils.escapeXml11(classAnnotation.description()) : className;

			String family = classAnnotation.family();
			if (family.isEmpty()) {
				family = (url.split("/").length > 1) ? url.split("/")[1] : "root";
			}
			String formatDefault = classAnnotation.formatDefault();

			Filer filer = processingEnv.getFiler();
			try {
				FileObject descXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
						"alfresco.extension.templates.webscripts" + wsFolder.replace("/", "."), 
						wsName + "." + method + ".desc.xml",
						annotatedClassElement);
				try (PrintWriter out = new PrintWriter(descXml.openWriter())) {
					out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
					out.println("<webscript>");
					out.println("	<shortname>" + shortName + "</shortname>");
					out.println("	<description>" + description + "</description>");
					out.println("	<url>" + StringEscapeUtils.escapeXml11(url) + "</url>");
					out.println("	<format default=\"" + formatDefault + "\">" + classAnnotation.format().name().toLowerCase() + "</format>");
					out.println("	<authentication>" + classAnnotation.authentication().name().toLowerCase() + "</authentication>");
					out.println("	<transaction allow=\"" + classAnnotation.transactionAllow().name().toLowerCase()+ "\">" + classAnnotation.transaction().name().toLowerCase() + "</transaction>");
					out.println("	<family>" + StringEscapeUtils.escapeXml11(family) + "</family>");
					out.println("</webscript>");

					out.flush();
				}
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + descXml.toUri() + " a été généré");
				
				FileObject springContextXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
						"alfresco.owsi",
						"wsgenerator" + wsFolder.replace("/", "-") + "-" + wsName + "-" + method + "-context.xml",
						annotatedClassElement);
				try (PrintWriter out = new PrintWriter(springContextXml.openWriter())) {
					out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
					out.println("<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN' 'http://www.springframework.org/dtd/spring-beans.dtd'>");
					out.println("<beans>");
					out.println("	<bean id=\"webscript" + wsFolder.replace("/", ".") + "." + wsName + "." + method 
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
