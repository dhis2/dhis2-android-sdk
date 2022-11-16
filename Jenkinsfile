pipeline {
    agent {
        label "ec2-android"
    }

    stages{
        stage('Checks') {
            steps {
                script {
                    echo 'Running Check style and quality'
                    sh './runChecks.sh'
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
            when { expression { false } }
            environment {
                BITRISE_GIT_BRANCH = "$env.GIT_BRANCH"
                BITRISE_GIT_BRANCH_DEST = "${env.CHANGE_TARGET == null ? env.GIT_BRANCH : env.CHANGE_TARGET}"
                BITRISE_PULL_REQUEST = "$env.CHANGE_ID"
                SONAR_TOKEN = credentials('android-sonarcloud-token')
            }
            steps {
                script {
                    echo 'Sonarqube job'
                    sh './gradlew sonarqube --stacktrace --no-daemon'
                }
            }
        }
        stage('Deploy') {
            environment {
                NEXUS_USERNAME = credentials('android-sonatype-nexus-username')
                NEXUS_PASSWORD = credentials('android-sonatype-nexus-password')
                GPG_KEY_ID = credentials('android-sdk-signing-public-key-id')
                GPG_PASSPHRASE = credentials('android-sdk-signing-private-key-password')
                GPG_KEY_LOCATION = "path_to_gpg_key"
            }
            steps {
                echo 'Deploy to Sonatype nexus'
                echo "$NEXUS_USERNAME"
                echo "$GPG_KEY_ID"
                sh './gradlew :core:publish'
            }
        }
    }
}
