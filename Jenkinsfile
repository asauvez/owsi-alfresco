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
				sh 'mvn -B clean source:jar deploy -Ddistribution=owsi-alfresco-release'
			}
			post {
				always {
					junit '**/target/surefire-reports/*.xml'
				}
				//changed {
				//	email (
				//		subject: "[Jenkins]: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' status changed",
				//		body: """<p>Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]':</p>
				//		<p>Check console output at &QUOT;<a href='${env.BUILD_URL}'>${env.JOB_NAME} [${env.BUILD_NUMBER}]</a>&QUOT;</p>""",
				//		recipientProviders: [[$class: 'DevelopersRecipientProvider']]
				//	)
				//}
			}
		}
		stage('Metrics sonar') {
			steps {
				sh 'mvn -Dsonar.organization="default-organization" -Dsonar.host.url=http://sonar.vitry.intranet:9000 sonar:sonar'
			}
		}
	}
}
