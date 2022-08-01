Description
===========
Permet de générer le code nécessaire pour :
- WS, y compris la doc Swagger
- Service Spring
- Cron
- Patch

Configuration
=============

Pour l'utiliser, dans le pom.xml du projet, ajouter :

	<dependencies>
		<dependency>
			<groupId>fr.openwide.alfresco</groupId>
			<artifactId>owsi-alfresco-repo-wsgenerator-annotation</artifactId>
			<version>${owsi-alfresco.version}</version>
		</dependency>
	</dependencies>

	<build>
		<plugins>
			<!-- Generate WS, Service and Cron XML -->
			<plugin>
				<groupId>org.bsc.maven</groupId>
				<artifactId>maven-processor-plugin</artifactId>
				<version>4.5</version>
				<executions>
					<execution>
						<id>maven-processor-plugin-default</id>
						<phase>generate-sources</phase>
						<goals>
							<goal>process</goal>
						</goals>
						<configuration>
							<processors>
								<processor>fr.openwide.alfresco.repo.wsgenerator.processor.GenerateWebScriptAnnotationProcessor</processor>
							</processors>
						</configuration>
					</execution>
				</executions>
				<dependencies>
					<dependency>
						<groupId>fr.openwide.alfresco</groupId>
						<artifactId>owsi-alfresco-repo-wsgenerator-processor</artifactId>
						<version>${project.version}</version>
					</dependency>
				</dependencies>
			</plugin>
		</plugins>
	</build>
