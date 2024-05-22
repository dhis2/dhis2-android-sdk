import org.jlleitschuh.gradle.ktlint.reporter.ReporterType

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
        maven(url = "https://maven.google.com")
        maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
        google()
        mavenCentral()
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-html/maven")
        maven(url = "https://jitpack.io")
    }
}

apply(from = "tasks.gradle.kts")

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")
    apply(plugin = "org.jetbrains.dokka")

    //group = GROUP
    //version = VERSION_NAME

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