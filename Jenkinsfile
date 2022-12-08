pipeline {
    agent {
        label "ec2-android"
    }

    stages{
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
                    echo 'Sonarqube job'
                    echo "$GIT_BRANCH"
                    echo "$GIT_BRANCH_DEST"
                    echo "$PULL_REQUEST"
                    if (GIT_BRANCH_DEST != null) {
                        // Fetch destination branch for Sonarqube comparision
                        remote = sh(returnStdout: true, script: 'git remote').trim()
                        url = sh(returnStdout: true, script: "git remote get-url $remote").trim()
                        sh "git fetch --no-tags --force --progress -- $url +refs/heads/$GIT_BRANCH_DEST:refs/remotes/$remote/$GIT_BRANCH_DEST"
                    }
                    sh './gradlew sonarqube --stacktrace --no-daemon'
                }
            }
        }
        stage('Deploy') {
            when {
                allOf {
                    // Do not deploy on PR builds
                    expression { env.CHANGE_ID == null }
                    expression { env.GIT_BRANCH == "master" }
                }
            }
            environment {
                NEXUS_USERNAME = credentials('android-sonatype-nexus-username')
                NEXUS_PASSWORD = credentials('android-sonatype-nexus-password')
                GPG_KEY_ID = credentials('android-sdk-signing-public-key-id')
                GPG_PASSPHRASE = credentials('android-sdk-signing-private-key-password')
                GPG_KEY_LOCATION = credentials('android-sdk-signing-private-key-ring-file')
            }
            steps {
                echo 'Deploy to Sonatype nexus'
                sh './gradlew :core:publish'
            }
        }
    }
}
