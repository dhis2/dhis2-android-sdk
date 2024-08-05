pipeline {
    agent {
        label "ec2-android"
    }

    options {
        disableConcurrentBuilds(abortPrevious: true)
    }

    stages{
        stage('Change to JAVA 17') {
            steps {
                script {
                    echo 'Changing JAVA version to 17'
                    sh 'sudo update-alternatives --set java /usr/lib/jvm/java-17-openjdk-amd64/bin/java'
                    env.JAVA_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
                }
            }
        }
        stage('Checks') {
            steps {
                script {
                    echo 'Running Check style and quality'
                    sh 'chmod +x ./runChecks.sh'
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
                GIT_BRANCH = "${env.GIT_BRANCH}"
                // Jenkinsfile considers empty value ('') as null
                GIT_BRANCH_DEST = "${env.CHANGE_TARGET == null ? '' : env.CHANGE_TARGET}"
                PULL_REQUEST = "${env.CHANGE_ID == null ? '' : env.CHANGE_ID }"
                SONAR_TOKEN = credentials('android-sonarcloud-token')
            }
            steps {
                script {
                    echo 'Sonarqube'
                    sh 'chmod +x ./scripts/sonarqube.sh'
                    retry(3) { // Retry up to 3 times
                        timeout(time: 10, unit: 'MINUTES') {
                            sh './scripts/sonarqube.sh'
                        }
                    }
                }
            }
        }
        stage('Deploy') {
            when {
                allOf {
                    // Do not deploy on PR builds
                    expression { env.CHANGE_ID == null }
                    anyOf {
                        expression { env.GIT_BRANCH == "develop" }
                        expression { env.GIT_BRANCH ==~ /[0-9]+\.[0-9]+\.[0-9]+-rc/ }
                    }
                }
            }
            environment {
                NEXUS_USERNAME = credentials('android-sonatype-nexus-username')
                NEXUS_PASSWORD = credentials('android-sonatype-nexus-password')
            }
            steps {
                echo 'Deploy to Sonatype nexus'
                sh './gradlew :core:publishToSonatype'
            }
        }
    }
}
