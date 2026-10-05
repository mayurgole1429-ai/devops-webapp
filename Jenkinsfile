pipeline {
    agent any

    tools {
        jdk 'JDK-17'
        maven 'Maven-3.9'
    }

    environment {
        DOCKERHUB = credentials('dockerhub-credentials')
        IMAGE_NAME = 'mayurgole1429-ai/devops-webapp'
    }

    options {
        timestamps()
    }

    stages {
        stage('1. Checkout') {
            steps {
                checkout scmGit(branches: [[name: '*/main']],
                                userRemoteConfigs: [[url: 'https://github.com/mayurgole1429-ai/devops-webapp.git']])
                echo "Checked out revision: ${env.GIT_COMMIT}"
            }
        }

        stage('2. Build (Maven)') {
            steps {
                bat 'mvn clean compile'
            }
            post {
                success { echo 'Maven build completed successfully.' }
            }
        }

        stage('3. Test (Selenium)') {
            steps {
                // Starts embedded Jetty, runs the Selenium tests, stops Jetty.
                // The Docker build stage runs only if all tests pass.
                bat 'mvn verify'
            }
            post {
                always {
                    junit allowEmptyResults: true, testResults: 'target/failsafe-reports/*.xml'
                }
            }
        }

        stage('4. Docker Image Build') {
            steps {
                bat "docker build -t %IMAGE_NAME%:%BUILD_NUMBER% -t %IMAGE_NAME%:latest ."
                bat "docker images | findstr %IMAGE_NAME%"
            }
        }

        stage('5. Push to Docker Hub') {
            steps {
                bat 'docker login -u %DOCKERHUB_USR% -p %DOCKERHUB_PSW%'
                bat "docker push %IMAGE_NAME%:%BUILD_NUMBER%"
                bat "docker push %IMAGE_NAME%:latest"
            }
            post {
                always {
                    bat 'docker logout'
                }
            }
        }
    }

    post {
        success {
            echo "Pipeline SUCCESS - image pushed: ${env.IMAGE_NAME}:${env.BUILD_NUMBER}"
        }
        failure {
            echo 'Pipeline FAILED - check the failed stage logs above.'
        }
    }
}
