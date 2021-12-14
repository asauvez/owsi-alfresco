package fr.openwide.alfresco.repo.wsgenerator.processor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
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

import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateBootstrapModel;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateCron;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GeneratePatch;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript;
import fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript.GenerateWebScriptLifecycle;
import fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint;
import fr.openwide.alfresco.repo.wsgenerator.model.WebScriptParam;

@SupportedSourceVersion(SourceVersion.RELEASE_8)
@SupportedAnnotationTypes({
	"fr.openwide.alfresco.repo.wsgenerator.annotation.WebScriptEndPoint",
	"fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateWebScript",
	"fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateService",
	"fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateBootstrapModel",
	"fr.openwide.alfresco.repo.wsgenerator.annotation.GeneratePatch",
	"fr.openwide.alfresco.repo.wsgenerator.annotation.GenerateCron",
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
		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(GenerateService.class)) {
			processService(annotatedClassElement);
		}
		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(GenerateBootstrapModel.class)) {
			processBootstrapModel(annotatedClassElement);
		}
		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(GeneratePatch.class)) {
			processPatch(annotatedClassElement);
		}
		for (Element annotatedClassElement : roundEnv.getElementsAnnotatedWith(GenerateCron.class)) {
			processScheduledJob(annotatedClassElement);
		}
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

		String family = generateWebScript.family().isEmpty() 
				? ((firstUrl.split("/").length > 1) ? firstUrl.split("/")[1] : "root")
				: generateWebScript.family();
		String formatDefault = generateWebScript.formatDefault().isEmpty() 
				? generateWebScript.formatDefaultEnum().name().toLowerCase() 
				: generateWebScript.formatDefault();

		String[] urlsFinal = urls;
		generateXml(annotatedClassElement, 
				"alfresco.extension.templates.webscripts" + wsFolder.replace("/", "."), 
				wsName + "." + method + ".desc.xml", (xml) -> {
			xml.writeStartElement("webscript");
			xml.writeStartElement("shortname"); xml.writeCharacters(shortName); xml.writeEndElement();
			xml.writeStartElement("description"); xml.writeCharacters(description.toString()); xml.writeEndElement();

			for (String url : urlsFinal) {
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
			
			if (   generateWebScript.cache().never() != true 
				|| generateWebScript.cache().isPublic() != false 
				|| generateWebScript.cache().mustrevalidate() != true) {
				xml.writeStartElement("cache");
				
				xml.writeStartElement("never");
				xml.writeCharacters(Boolean.toString(generateWebScript.cache().never()));
				xml.writeEndElement(); // never

				xml.writeStartElement("public");
				xml.writeCharacters(Boolean.toString(generateWebScript.cache().isPublic()));
				xml.writeEndElement(); // public

				xml.writeStartElement("mustrevalidate");
				xml.writeCharacters(Boolean.toString(generateWebScript.cache().mustrevalidate()));
				xml.writeEndElement(); // mustrevalidate

				xml.writeEndElement(); // cache
			}

			if (generateWebScript.lifecycle() != GenerateWebScriptLifecycle.DEFAULT) {
				xml.writeStartElement("lifecycle"); xml.writeCharacters(generateWebScript.lifecycle().name().toLowerCase()); xml.writeEndElement();
			}
			
			xml.writeEndElement(); // webscript
		});
		
		if (generateWebScript.useViewFile()) {
			try {
				Filer filer = processingEnv.getFiler();
				FileObject viewFile = filer.getResource(StandardLocation.CLASS_PATH, 
						"alfresco.extension.templates.webscripts" + wsFolder.replace("/", "."), 
						wsName + "." + method + "." + formatDefault + ".ftl");
				processingEnv.getMessager().printMessage(Diagnostic.Kind.NOTE, "Le fichier " + viewFile.toUri() + " existe bien");
			} catch (FileNotFoundException ex) {
				throw new IllegalStateException("If you declare useViewFile=true, you must create a file "
						+ "src/main/resources/alfresco/extension/templates/webscripts" + wsFolder + "/" 
						+ wsName + "." + method + "." + formatDefault + ".ftl");
			} catch (IOException e) {
				throw new IllegalStateException(e);
			}
		}
		
		String methodFinal = method;
		generateSpringContext(annotatedClassElement, (xml) -> {
			xml.writeStartElement("bean");
			xml.writeAttribute("id", "webscript" + wsFolder.replace("/", ".") + "." + wsName + "." + methodFinal);
			xml.writeAttribute("parent", generateWebScript.beanParent());
			xml.writeAttribute("class", className.toString());
			xml.writeEndElement(); // bean
		});
	}

	private void processService(Element annotatedClassElement) throws FactoryConfigurationError {
		GenerateService generateService = annotatedClassElement.getAnnotation(GenerateService.class);
		Name className = ((TypeElement) annotatedClassElement).getQualifiedName();

		generateSpringContext(annotatedClassElement, (xml) -> {
			String serviceName = generateService.id();
			if (serviceName.isEmpty()) {
				serviceName = className.toString();
			}
			
			xml.writeStartElement("bean");
			xml.writeAttribute("id", serviceName);
			xml.writeAttribute("class", className.toString());
			if (generateService.dependsOn().length > 0) {
				xml.writeAttribute("depends-on", String.join(", ", generateService.dependsOn()));
			}
			xml.writeEndElement(); // bean
		});
	}

	private void processBootstrapModel(Element annotatedClassElement) throws FactoryConfigurationError {
		GenerateBootstrapModel generateBootstrapModel = annotatedClassElement.getAnnotation(GenerateBootstrapModel.class);
		Name className = ((TypeElement) annotatedClassElement).getQualifiedName();

		generateSpringContext(annotatedClassElement, (xml) -> {
			String modelName = generateBootstrapModel.id();
			if (modelName.isEmpty()) {
				modelName = className.toString();
			}
			
			xml.writeStartElement("bean");
			xml.writeAttribute("id", modelName);
			xml.writeAttribute("parent", "dictionaryModelBootstrap");
			if (generateBootstrapModel.dependsOn().length > 0) {
				xml.writeAttribute("depends-on", String.join(", ", generateBootstrapModel.dependsOn()));
			}
			if (generateBootstrapModel.importModels().length != 0) {
				xml.writeStartElement("property");
				xml.writeAttribute("name", "models");
				xml.writeStartElement("list");
				for (String model : generateBootstrapModel.importModels()) {
					xml.writeStartElement("value");
					xml.writeCharacters(model);
					xml.writeEndElement(); // value
				}
				xml.writeEndElement(); // list
				xml.writeEndElement(); // property
			}
			if (generateBootstrapModel.importLabels().length != 0) {
				xml.writeStartElement("property");
				xml.writeAttribute("name", "labels");
				xml.writeStartElement("list");
				for (String label : generateBootstrapModel.importLabels()) {
					if (label.endsWith(".properties")) {
						label = label.substring(0, label.length() - ".properties".length());
					}
					xml.writeStartElement("value");
					xml.writeCharacters(label);
					xml.writeEndElement(); // value
				}
				xml.writeEndElement(); // list
				xml.writeEndElement(); // property
			}
			xml.writeEndElement(); // bean
		});
	}

	private void processPatch(Element annotatedClassElement) throws FactoryConfigurationError {
		GeneratePatch generatePatch = annotatedClassElement.getAnnotation(GeneratePatch.class);
		Name className = ((TypeElement) annotatedClassElement).getQualifiedName();

		generateSpringContext(annotatedClassElement, (xml) -> {
			String patchName = generatePatch.id();
			if (patchName.isEmpty()) {
				patchName = className.toString();
			}

			xml.writeStartElement("bean");
			xml.writeAttribute("id", "patch." + patchName);
			xml.writeAttribute("class", className.toString());
			xml.writeAttribute("parent", "basePatch");
			if (generatePatch.dependsOn().length > 0) {
				xml.writeAttribute("depends-on", String.join(", ", generatePatch.dependsOn()));
			}

			generateProperty(xml, "id", "patch." + patchName);
			generateProperty(xml, "description", "OWSI auto generate patch patch." + patchName);
			generateProperty(xml, "fixesFromSchema", "0");
			generateProperty(xml, "fixesToSchema", "${version.schema}");
			generateProperty(xml, "targetSchema", "10000000");

			xml.writeEndElement(); // bean
		});
	}
	
	private void processScheduledJob(Element annotatedClassElement) {
		GenerateCron generateCron = annotatedClassElement.getAnnotation(GenerateCron.class);
		String className = ((TypeElement) annotatedClassElement).getQualifiedName().toString();
		String serviceId = (generateCron.id().isEmpty()) ? className : "cron." + className;
		
		generateSpringContext(annotatedClassElement, (xml) -> {
			String startDelay = generateCron.startDelay();
			if (startDelay.startsWith("P")) {
				startDelay = Long.toString(Duration.parse(generateCron.startDelay()).get(ChronoUnit.SECONDS)*1000L);
			}
			
			xml.writeStartElement("bean");
			xml.writeAttribute("id", serviceId);
			xml.writeAttribute("class", className.toString());
			if (generateCron.dependsOn().length > 0) {
				xml.writeAttribute("depends-on", String.join(", ", generateCron.dependsOn()));
			}
			xml.writeEndElement(); // bean
			
			String jobDetailId = serviceId + ".JobDetail";
			xml.writeStartElement("bean");
			xml.writeAttribute("id", jobDetailId);
			xml.writeAttribute("class", "org.springframework.scheduling.quartz.JobDetailFactoryBean");
			generateProperty(xml, "jobClass", "fr.openwide.alfresco.repo.core.cron.model.CronRunnableJob");
			xml.writeStartElement("property");
			xml.writeAttribute("name", "jobDataAsMap");
			xml.writeStartElement("map");
			
			xml.writeStartElement("entry");
			xml.writeAttribute("key", "runnable");
			xml.writeAttribute("value-ref", serviceId);
			xml.writeEndElement(); // entry
			
			xml.writeStartElement("entry");
			xml.writeAttribute("key", "jobLockService");
			xml.writeAttribute("value-ref", "jobLockService");
			xml.writeEndElement(); // entry
			
			xml.writeStartElement("entry");
			xml.writeAttribute("key", "transactionService");
			xml.writeAttribute("value-ref", "transactionService");
			xml.writeEndElement(); // entry
			
			generateMapEntry(xml, "logAsInfo", Boolean.toString(generateCron.logAsInfo()));
			generateMapEntry(xml, "readOnly", Boolean.toString(generateCron.readOnly()));
			generateMapEntry(xml, "enable", generateCron.enable());
			generateMapEntry(xml, "runAs", generateCron.runAs());

			xml.writeEndElement(); // map
			xml.writeEndElement(); // property
			xml.writeEndElement(); // bean
			
			String cronTriggerFactoryBeanId = serviceId + ".CronTriggerFactoryBean";
			xml.writeStartElement("bean");
			xml.writeAttribute("id", cronTriggerFactoryBeanId);
			xml.writeAttribute("class", "org.springframework.scheduling.quartz.CronTriggerFactoryBean");
			xml.writeStartElement("property");
			xml.writeAttribute("name", "jobDetail");
			xml.writeAttribute("ref", jobDetailId);
			xml.writeEndElement(); // property
			generateProperty(xml, "cronExpression", generateCron.cronExpression());
			generateProperty(xml, "startDelay", startDelay);
			xml.writeEndElement(); // bean
			
			xml.writeStartElement("bean");
			xml.writeAttribute("id", serviceId + ".customTriggersList");
			xml.writeAttribute("class", "org.springframework.scheduling.quartz.SchedulerAccessorBean");
			xml.writeStartElement("property");
			xml.writeAttribute("name", "scheduler");
			xml.writeAttribute("ref", "schedulerFactory");
			xml.writeEndElement(); // property
			xml.writeStartElement("property");
			xml.writeAttribute("name", "triggers");
			xml.writeStartElement("list");
			xml.writeStartElement("ref");
			xml.writeAttribute("bean", cronTriggerFactoryBeanId);
			xml.writeEndElement(); // ref
			xml.writeEndElement(); // list
			xml.writeEndElement(); // property

			xml.writeEndElement(); // bean
		});
	}

	@FunctionalInterface
	private interface XmlGenerator {
		void generate(XMLStreamWriter xml) throws XMLStreamException;
	}
	
	private void generateSpringContext(Element annotatedClassElement, XmlGenerator generator) {
		generateXml(annotatedClassElement, 
				"org.springframework.extensions.webscripts", 
				"wsgenerator-" + annotatedClassElement + "-context.xml", (xml) -> {
			xml.writeStartElement("beans");
			xml.writeDefaultNamespace("http://www.springframework.org/schema/beans");
			xml.writeNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			xml.writeAttribute("xsi", "http://www.w3.org/2001/XMLSchema-instance", "schemaLocation", 
					"http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans-2.5.xsd");

			generator.generate(xml);
			
			xml.writeEndElement(); // beans
		});
	}

	private void generateXml(Element annotatedClassElement, String module, String name, XmlGenerator generator) {
		Filer filer = processingEnv.getFiler();
		try {
			FileObject springContextXml = filer.createResource(StandardLocation.CLASS_OUTPUT, 
					module, name, annotatedClassElement);
			try (Writer out = springContextXml.openWriter()) {
				XMLStreamWriter xml = new IndentingXMLStreamWriter(XMLOutputFactory.newFactory().createXMLStreamWriter(out));
				xml.writeStartDocument();
				xml.writeComment("Generated by " + getClass() + " for " + annotatedClassElement);
				
				generator.generate(xml);
				
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

	private void generateProperty(XMLStreamWriter xml, String propertyName, String propertyValue) throws XMLStreamException {
		xml.writeStartElement("property");
		xml.writeAttribute("name", propertyName);
		xml.writeAttribute("value", propertyValue);
		xml.writeEndElement(); // property
	}
	private void generateMapEntry(XMLStreamWriter xml, String key, String value) throws XMLStreamException {
		xml.writeStartElement("entry");
		xml.writeAttribute("key", key);
		xml.writeAttribute("value", value);
		xml.writeEndElement(); // entry
	}

	private String getFullName(Element element) {
		Element parent = element.getEnclosingElement();
		return ((parent != null) ? parent + "." : "") + element.getSimpleName().toString();
	}

}
