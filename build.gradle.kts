import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

group = "org.hisp.dhis"
version = libs.versions.dhis2AndroidSdkVersion.get()

/**
 * Property from the Gradle command line. To remove the snapshot suffix from the version.
 */
if (project.hasProperty("removeSnapshotSuffix")) {
    val mainVersion = (version as String).split("-SNAPSHOT")[0]
    version = mainVersion
}

buildscript {
    repositories {
        google()
        mavenLocal()
        mavenCentral()
        maven(url = "https://plugins.gradle.org/m2/")
    }

    dependencies {
        classpath(libs.gradle)
        classpath(libs.kotlin)
        classpath(libs.ktlint)
    }
}

plugins {
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.dokka)
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.cyclonedx)
}

sonarqube {
    properties {
        val branch = System.getenv("GIT_BRANCH")
        val targetBranch = System.getenv("GIT_BRANCH_DEST")
        val pullRequestId = System.getenv("PULL_REQUEST")

        property("sonar.projectKey", "dhis2_dhis2-android-sdk")
        property("sonar.organization", "dhis2")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectName", "dhis2-android-sdk")

        if (pullRequestId == null) {
            property("sonar.branch.name", branch)
        } else {
            property("sonar.pullrequest.base", targetBranch)
            property("sonar.pullrequest.branch", branch)
            property("sonar.pullrequest.key", pullRequestId)
        }
    }
}

allprojects {
    repositories {
        mavenCentral()
        google()
        maven(url = "https://maven.google.com")
        maven(url = "https://central.sonatype.com/repository/maven-snapshots/")
        maven(url = "https://jitpack.io")
    }
}

apply(from = "tasks.gradle.kts")

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.dokka")

    configure<org.jlleitschuh.gradle.ktlint.KtlintExtension> {
        version.set("0.50.0")
        android.set(true)
        outputColorName.set("RED")
        reporters {
            reporter(ReporterType.PLAIN)
            reporter(ReporterType.CHECKSTYLE)
        }
    }
}

val nexusUsername: String? = System.getenv("NEXUS_USERNAME")
val nexusPassword: String? = System.getenv("NEXUS_PASSWORD")

nexusPublishing {
    this.repositories {
        sonatype {
            nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
            snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
            username.set(nexusUsername)
            password.set(nexusPassword)
        }
    }
}