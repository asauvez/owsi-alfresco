pipeline {
   agent {
      label 'java'
   }
   tools {
      maven 'Maven 3.3.9'
      jdk 'OpenJDK 11.0.2'
   }
   stages {
      stage('Build and Test') {
         steps {
            sh 'mvn -B -U clean package -Damf -Dlivraison'
         }
      }
   }
}
