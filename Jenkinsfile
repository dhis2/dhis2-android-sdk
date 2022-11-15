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
            environment {
                BITRISE_GIT_BRANCH = "$env.GIT_BRANCH"
                BITRISE_GIT_BRANCH_DEST = "${env.CHANGE_TARGET == null ? env.GIT_BRANCH : env.CHANGE_TARGET}"
                BITRISE_PULL_REQUEST = "$env.CHANGE_ID"
                SONAR_TOKEN = credentials('android-sonarcloud-token')
            }
            steps {
                script {
                    echo 'Sonarqube job'
                    echo "$env"
                    echo "$BITRISE_GIT_BRANCH"
                    echo "$BITRISE_GIT_BRANCH_DEST"
                    echo "$BITRISE_PULL_REQUEST"
                    sh './gradlew sonarqube --stacktrace --no-daemon'
                }
            }
        }
        stage('Deploy to nexus') {
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
