pipeline {
    agent {
        label "ec2-android"
    }

    stages{
        stage('Deploy') {
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
