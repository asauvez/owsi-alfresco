pipeline {
   agent {
      label 'java'
   }
   tools {
      maven 'Maven 3.3.9'
      jdk 'JDK 1.8 (update 101)'
   }
   stages {
      stage('Build and Test') {
         steps {
	    dir("owsi-alfresco") {
	            sh 'mvn -B -U clean package -Damf -Dlivraison'
	    }
         }
      }
   }
}
