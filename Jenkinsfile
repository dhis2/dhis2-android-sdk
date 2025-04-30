def retryOnTimeout(retries, timeoutMinutes, script) {
    def success = false
    for (int i = 0; i < retries; i++) {
        try {
            timeout(time: timeoutMinutes, unit: 'MINUTES') {
                script()
            }
            success = true
            break
        } catch (org.jenkinsci.plugins.workflow.steps.FlowInterruptedException e) {
            echo "Timeout occurred, retrying... (${i + 1}/${retries})"
        } catch (Exception e) {
            throw e
        }
    }
    if (!success) {
        error "Failed after ${retries} retries due to timeout"
    }
}

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
                    retryOnTimeout(3, 20) {
                        sh './runChecks.sh'
                    }
                }
            }
        }
        stage('Api validation') {
            steps {
                script {
                    echo 'Running public API validation'
                    retryOnTimeout(3, 10) {
                        sh './gradlew :core:apiCheck'
                    }
                }
            }
        }
        stage('Unit tests') {
            steps {
                script {
                    echo 'Running unit tests'
                    retryOnTimeout(3, 15) {
                        sh './gradlew testDebugUnitTest --stacktrace --no-daemon'
                    }
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
                    retryOnTimeout(3, 20) {
                        sh './scripts/browserstackJenkins.sh'
                    }
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
                    retryOnTimeout(3, 10) {
                        sh './scripts/sonarqube.sh'
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
                        expression { env.GIT_BRANCH ==~ /[0-9]+\.[0-9]+\.[0-9]+(\.[0-9]+)?-rc/ }
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
