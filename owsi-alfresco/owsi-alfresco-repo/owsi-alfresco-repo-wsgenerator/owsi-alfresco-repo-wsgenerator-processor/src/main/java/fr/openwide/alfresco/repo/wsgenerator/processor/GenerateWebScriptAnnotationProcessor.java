package fr.openwide.alfresco.repo.wsgenerator.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
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
import javax.lang.model.type.MirroredTypeException;
import javax.tools.Diagnostic;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
	"fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint",
	"fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript"
})
public class GenerateWebScriptAnnotationProcessor extends AbstractProcessor {

	// On écrit dans /tmp les URL, car le build peut se faire en deux fois
	private File API_FOLDER = new File(System.getProperty("java.io.tmpdir"), getClass().getName());

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(WebScriptEndPoint.class)) {
			WebScriptEndPoint webScriptEndPoint = annotatedClassElement.getAnnotation(WebScriptEndPoint.class);
			
			API_FOLDER.mkdirs();
			File apiUrlFile = new File(API_FOLDER, getFullName(annotatedClassElement));
			try (PrintWriter out = new PrintWriter(new FileWriter(apiUrlFile))) {
				out.println(webScriptEndPoint.method().name());
				out.println(webScriptEndPoint.url());
				out.flush();
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		
		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(GenerateWebScript.class)) {
			GenerateWebScript generateWebScript = annotatedClassElement.getAnnotation(GenerateWebScript.class);
			Name className = ((TypeElement) annotatedClassElement).getQualifiedName();

			String[] urls = generateWebScript.url();
			String method = generateWebScript.method().name().toLowerCase();
			
			try {
				generateWebScript.paramClass();
			} catch( MirroredTypeException mte ) {
				String paramClass = mte.getTypeMirror().toString();
				if (! WebScriptParam.class.getName().equals(paramClass)) {
					if (urls.length != 0) {
						throw new IllegalStateException(getFullName(annotatedClassElement) + " : When declaring @GenerateWebScript.paramClass(), url shoud not be defined.");
					}
					if (! "get".equals(method)) {
						throw new IllegalStateException(getFullName(annotatedClassElement) + " : When declaring @GenerateWebScript.paramClass(), method shoud not be defined.");
					}
					File apiUrlFile = new File(API_FOLDER, paramClass);
					if (! apiUrlFile.canRead()) {
						throw new IllegalStateException(getFullName(annotatedClassElement) + " : When declaring @GenerateWebScript.paramClass(), API project should be build first.");
					}
					try (BufferedReader in = new BufferedReader(new FileReader(apiUrlFile))) {
						method = in.readLine().toLowerCase();
						urls = new String[] { in.readLine() };
					} catch (IOException e) {
						throw new IllegalStateException(e);
					}
				}
			}
			
			String firstUrl = (urls.length > 0) ? urls[0] : "";
			String wsFolder = (! generateWebScript.wsFolder().isEmpty()) ? generateWebScript.wsFolder() : StringUtils.substringBeforeLast(firstUrl, "/");
			String wsName = (! generateWebScript.wsName().isEmpty()) ? generateWebScript.wsName() : StringUtils.substringBefore(StringUtils.substringAfterLast(firstUrl, "/"), "?").replace(".", "-");

			String shortName = (! generateWebScript.shortName().isEmpty()) ? StringEscapeUtils.escapeXml11(generateWebScript.shortName()) : wsName;
			CharSequence description = (! generateWebScript.description().isEmpty()) ? StringEscapeUtils.escapeXml11(generateWebScript.description()) : className;

			String family = generateWebScript.family();
			if (family.isEmpty()) {
				family = (firstUrl.split("/").length > 1) ? firstUrl.split("/")[1] : "root";
			}
			String formatDefault = generateWebScript.formatDefault();

			Filer filer = processingEnv.getFiler();
			try {
				FileObject descXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
						"alfresco.extension.templates.webscripts" + wsFolder.replace("/", "."), 
						wsName + "." + method + ".desc.xml",
						annotatedClassElement);
				try (PrintWriter out = new PrintWriter(descXml.openWriter())) {
					out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
					out.println("<!-- Generated by " + getClass() + " for " + annotatedClassElement + " -->");
					out.println("<webscript>");
					out.println("	<shortname>" + shortName + "</shortname>");
					out.println("	<description>" + description + "</description>");
					for (String url : urls) {
						out.println("	<url>" + StringEscapeUtils.escapeXml11(url) + "</url>");
					}
					out.println("	<format default=\"" + formatDefault + "\">" + generateWebScript.format().name().toLowerCase() + "</format>");
					out.println("	<authentication>" + generateWebScript.authentication().name().toLowerCase() + "</authentication>");
					out.println("	<transaction allow=\"" + generateWebScript.transactionAllow().name().toLowerCase()+ "\">" + generateWebScript.transaction().name().toLowerCase() + "</transaction>");
					out.println("	<family>" + StringEscapeUtils.escapeXml11(family) + "</family>");
					out.println("</webscript>");

					out.flush();
				}
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + descXml.toUri() + " a été généré");
				
				if (generateWebScript.useViewFile()) {
					try {
						FileObject viewFile = filer.getResource(StandardLocation.CLASS_PATH, 
								"alfresco.extension.templates.webscripts" + wsFolder.replace("/", "."), 
								wsName + "." + method + "." + formatDefault + ".ftl");
						processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + viewFile.toUri() + " existe bien");
					} catch (FileNotFoundException ex) {
						throw new IllegalStateException("If you declare useViewFile=true, you must create a file "
								+ "src/main/resources/alfresco/extension/templates/webscripts" + wsFolder + "/" 
								+ wsName + "." + method + "." + formatDefault + ".ftl");
					}
				}
				
				FileObject springContextXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
						"org.springframework.extensions.webscripts",
						"wsgenerator" + wsFolder.replace("/", "-") + "-" + wsName + "-" + method + "-context.xml",
						annotatedClassElement);
				try (PrintWriter out = new PrintWriter(springContextXml.openWriter())) {
					out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
					out.println("<!DOCTYPE beans PUBLIC '-//SPRING//DTD BEAN//EN' 'http://www.springframework.org/dtd/spring-beans.dtd'>");
					out.println("<!-- Generated by " + getClass() + " for " + annotatedClassElement + " -->");
					out.println("<beans>");
					out.println("	<bean id=\"webscript" + wsFolder.replace("/", ".") + "." + wsName + "." + method 
							+ "\" parent=\"" + generateWebScript.beanParent() + "\"");
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

	private String getFullName(Element element) {
		Element parent = element.getEnclosingElement();
		return ((parent != null) ? parent + "." : "") + element.getSimpleName().toString();
	}

}
