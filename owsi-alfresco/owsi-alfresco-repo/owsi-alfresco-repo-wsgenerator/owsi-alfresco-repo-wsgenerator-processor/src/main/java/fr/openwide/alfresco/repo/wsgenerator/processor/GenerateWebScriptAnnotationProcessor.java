package fr.openwide.alfresco.repo.wsgenerator.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
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
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

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
			processWsEndPoint(annotatedClassElement);
		}
		
		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(GenerateWebScript.class)) {
			processWs(annotatedClassElement);
		}
		
//		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(GenerateScheduledJob.class)) {
//			processScheduledJob(annotatedClassElement);
//		}
		return true;
	}

	private void processWsEndPoint(Element annotatedClassElement) {
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

	private void processWs(Element annotatedClassElement) throws FactoryConfigurationError {
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
		String wsFolder = (! generateWebScript.wsFolder().isEmpty()) ? generateWebScript.wsFolder() : StringUtils.substringBeforeLast(firstUrl, "/").replace('{', '_').replace('}', '_');
		String wsName = (! generateWebScript.wsName().isEmpty()) ? generateWebScript.wsName() : StringUtils.substringBefore(StringUtils.substringAfterLast(firstUrl, "/"), "?").replace(".", "-").replace('{', '_').replace('}', '_');

		String shortName = (! generateWebScript.shortName().isEmpty()) ? StringEscapeUtils.escapeXml10(generateWebScript.shortName()) : wsName;
		CharSequence description = (! generateWebScript.description().isEmpty()) ? StringEscapeUtils.escapeXml10(generateWebScript.description()) : className;

		String family = generateWebScript.family();
		if (family.isEmpty()) {
			family = (firstUrl.split("/").length > 1) ? firstUrl.split("/")[1] : "root";
		}
		String formatDefault = generateWebScript.formatDefault();
		if (formatDefault.isEmpty()) {
			formatDefault = generateWebScript.formatDefaultEnum().name().toLowerCase();
		}

		Filer filer = processingEnv.getFiler();
		try {
			FileObject descXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
					"alfresco.extension.templates.webscripts" + wsFolder.replace("/", "."), 
					wsName + "." + method + ".desc.xml",
					annotatedClassElement);
			try (Writer out = descXml.openWriter()) {
				XMLStreamWriter xml = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
				xml.writeStartDocument();
				xml.writeComment("Generated by " + getClass() + " for " + annotatedClassElement);
				
				xml.writeStartElement("webscript");
				xml.writeDefaultNamespace("http://www.springframework.org/schema/beans");
				
				xml.writeStartElement("shortname"); xml.writeCharacters(shortName); xml.writeEndElement();
				xml.writeStartElement("description"); xml.writeCharacters(description.toString()); xml.writeEndElement();

				for (String url : urls) {
					xml.writeStartElement("url"); xml.writeCharacters(url); xml.writeEndElement();
				}
				
				xml.writeStartElement("format"); 
				xml.writeAttribute("default", formatDefault);
				xml.writeCharacters(generateWebScript.format().name().toLowerCase()); 
				xml.writeEndElement();
				
				xml.writeStartElement("authentication"); xml.writeCharacters(generateWebScript.authentication().name().toLowerCase()); xml.writeEndElement();
				
				xml.writeStartElement("transaction"); 
				xml.writeAttribute("allow", generateWebScript.transactionAllow().name().toLowerCase());
				xml.writeCharacters(generateWebScript.transaction().name().toLowerCase()); 
				xml.writeEndElement();

				xml.writeStartElement("family"); xml.writeCharacters(family); xml.writeEndElement();

				xml.writeEndElement(); // webscript
				
				xml.writeEndDocument();
				xml.flush();
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
			try (Writer out = springContextXml.openWriter()) {
				XMLStreamWriter xml = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
				xml.writeStartDocument();
				xml.writeComment("Generated by " + getClass() + " for " + annotatedClassElement);
				
				xml.writeStartElement("beans");
				xml.writeDefaultNamespace("http://www.springframework.org/schema/beans");
				xml.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
				xml.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", 
						"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd");

				xml.writeStartElement("bean");
				xml.writeAttribute("id", "webscript" + wsFolder.replace("/", ".") + "." + wsName + "." + method);
				xml.writeAttribute("parent", generateWebScript.beanParent());
				xml.writeAttribute("class", className.toString());
				xml.writeEndElement(); // bean
				
				xml.writeEndElement(); // beans
				
				xml.writeEndDocument();
				xml.flush();
			}
			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + springContextXml.toUri() + " a été généré");
		} catch (IOException e) {
			throw new IllegalStateException(e);
		} catch (XMLStreamException e) {
			throw new IllegalStateException(e);
		}
	}

//	private void processScheduledJob(Element annotatedClassElement) {
//		GenerateScheduledJob scheduledJob = annotatedClassElement.getAnnotation(GenerateScheduledJob.class);
//		String className = ((TypeElement) annotatedClassElement).getQualifiedName().toString();
//
//		Filer filer = processingEnv.getFiler();
//		try {
//			FileObject springContextXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
//					"alfresco.extension",
//					"scheduler-" + className + "-context.xml",
//					annotatedClassElement);
//			try (Writer out = springContextXml.openWriter()) {
//				XMLStreamWriter xml = XMLOutputFactory.newFactory().createXMLStreamWriter(out);
//				xml.writeDefaultNamespace("http://www.springframework.org/schema/beans");
//				xml.writeStartDocument();
//				xml.writeComment("Generated by " + getClass() + " for " + annotatedClassElement);
//				
//				xml.writeStartElement("beans");
//
//				xml.writeStartDocument("bean");
//				xml.writeAttribute("id", "scheduler" + className);
//				xml.writeAttribute("class", className.toString());
//				if (scheduledJob.beanParent().length() != 0) {
//					xml.writeAttribute("parent", scheduledJob.beanParent());
//				}
//				xml.writeEndElement(); // bean
//				
//				
///* 
// * 
// * 	<bean id="sogelym.unshare.unshareJobDetail" class="org.springframework.scheduling.quartz.JobDetailBean">
//		<property name="jobClass" value="fr.openwide.sogelym.repo.unshare.service.impl.SogelymUnshareCron"/>
//		<property name="jobDataAsMap">
//			<map>
//				<entry key="keepSharedDays" value="${sogelym.unshare.days}"/>
//				<entry key="nodeModelService" value-ref="owsi.service.nodeModelService"/>
//				<entry key="nodeSearchModelService" value-ref="owsi.service.nodeSearchModelService"/>
//			</map>
//		</property>
//	</bean>
//	<bean id="sogelym.unshareJobTrigger" class="org.alfresco.util.CronTriggerBean">
//		<property name="jobDetail">
//			<ref bean="sogelym.unshare.unshareJobDetail" />
//		</property>
//		<property name="scheduler">
//			<ref bean="schedulerFactory" />
//		</property>
//		<property name="cronExpression">
//			<value>${sogelym.unshare.cron}</value>
//		</property>
//	</bean>
//
// * */				
//				
//				
//				xml.writeEndElement(); // beans
//				
//				xml.writeEndDocument();
//				xml.flush();
//			}
//			processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + springContextXml.toUri() + " a été généré");
//		} catch (IOException e) {
//			throw new IllegalStateException(e);
//		} catch (XMLStreamException e) {
//			throw new IllegalStateException(e);
//		}
//	}

	private String getFullName(Element element) {
		Element parent = element.getEnclosingElement();
		return ((parent != null) ? parent + "." : "") + element.getSimpleName().toString();
	}

}
