pipeline {
	agent {
		label 'java'
	}
	tools {
		maven 'Maven 3.3.9'
		jdk 'JDK 1.8 (update 101)'
		// jdk 'OpenJDK 11.0.2' 
	}
	stages {
		stage('Build and Test') {
			steps {
				dir("owsi-alfresco") {
					sh 'mvn -B clean source:jar deploy -Ddistribution=owsi-alfresco-release'
				}
			}
			post {
				always {
					junit '**/target/surefire-reports/*.xml'
				}
			}
		}
		stage('Metrics sonar') {
			steps {
				dir("owsi-alfresco") {
					sh 'mvn -Dsonar.organization="default-organization" -Dsonar.host.url=http://sonar.vitry.intranet:9000 sonar:sonar'
				}
			}
		}
	}
}
