pipeline {
    agent {
        label "ec2-android"
    }

    stages{
        stage('Clone Branch') {
            when { expression { false } }
            steps {
                git url: 'https://github.com/dhis2/dhis2-android-sdk', branch: 'test-lab-app'
                script {
                    echo 'Clonning DHIS2 Android SDK develop branch and updating submodules'
                }
            }
        }
        stage('Checks') {
            steps {
                script {
                    echo 'Running Check style and quality'
                    sh './runChecks.sh'
                    sh 'echo test'
                }
            }
        }

        stage('Unit tests') {
            steps {
                script {
                    echo 'Running unit tests'
                    sh './gradlew testDebugUnitTest --stacktrace --no-daemon'
                }
            }
        }
        stage('Instrumented tests') {
            environment {
                BROWSERSTACK = credentials('android-browserstack')
            }
            steps {
                script {
                    echo 'Browserstack deployment and running tests'
                    sh 'chmod +x ./scripts/browserstackJenkins.sh'
                    sh './scripts/browserstackJenkins.sh'
                }
            }
        }
        stage('JaCoCo report') {
            steps {
                script {
                    echo 'JaCoCo report'
                    sh './gradlew jacocoReport --stacktrace --no-daemon'
                }
            }
        }
        stage('Sonarqube') {
            environment {
                BITRISE_GIT_BRANCH = "test-lab-app"
                BITRISE_GIT_BRANCH_DEST = "develop"
                SONAR_TOKEN = credentials('android-sonarcloud-token')
            }
            steps {
                script {
                    echo 'Sonarqube job'
                    sh './gradlew sonarqube --stacktrace --no-daemon'
                }
            }
        }
        stage('Deploy to nexus') {
            when {
                expression { false }
            }
            environment {
                NEXUS_USERNAME = ""
                NEXUS_PASSWORD = ""
                GPG_KEY_ID = ""
                GPG_PASSPHRASE = ""
            }
            steps {
                echo 'Browserstack deployment and running tests'
                sh 'chmod +x ./scripts/browserstackJenkins.sh'
                sh './scripts/deploy_to_nexus.sh'
            }
        }
    }
}
