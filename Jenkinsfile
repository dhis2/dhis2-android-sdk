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

    environment {
        GRADLE_USER_HOME = "${env.WORKSPACE}/.gradle"
        // Improvement 1 & 2: Increase memory and disable daemon for stability
        GRADLE_OPTS = "-Xmx4096m -XX:MaxMetaspaceSize=1024m -XX:+HeapDumpOnOutOfMemoryError -Dorg.gradle.daemon=false"
    }


    options {
        disableConcurrentBuilds(abortPrevious: true)
    }

    stages{
        stage('Clean Gradle Cache Periodically') {
            when {
                // Improvement 4: Clean cache every 10 builds to prevent corruption
                expression { currentBuild.number % 10 == 0 }
            }
            steps {
                script {
                    echo "Periodic cache cleanup (build #${currentBuild.number})"
                    sh 'rm -rf ${GRADLE_USER_HOME}/caches'
                    sh 'rm -rf ${GRADLE_USER_HOME}/daemon'
                }
            }
        }
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
        // Deploy stage removed - now handled by GitHub Actions workflow
        // See .github/workflows/deploy.yml for deployment configuration
    }
    post {
        always {
            script {
                // Improvement 3: Always stop daemon and clean locks after build
                echo 'Cleaning up Gradle daemon and locks'
                sh './gradlew --stop || true'
                sh 'rm -rf ${GRADLE_USER_HOME}/daemon || true'
                sh 'rm -rf ${GRADLE_USER_HOME}/caches/*.lock || true'
            }
        }
        failure {
            sendNotification(env.GIT_BRANCH, '*Build Failed*\n', 'bad')
        }
    }
}

def sendNotification(String branch, String messagePrefix, String color){
   slackSend channel: '#android-sdk-dev', color: color, message: messagePrefix+ custom_msg()
}

def custom_msg(){
  def BUILD_URL= env.BUILD_URL
  def JOB_NAME = env.JOB_NAME
  def BUILD_ID = env.BUILD_ID
  def BRANCH_NAME = env.GIT_BRANCH
  def JENKINS_LOG= "*Job:* $JOB_NAME\n *Branch:* $BRANCH_NAME\n *Build Number:* $BUILD_NUMBER (<${BUILD_URL}|Open>)"
  return JENKINS_LOG
}
