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
    alias(libs.plugins.dokka)
    alias(libs.plugins.nexus.publish)
    alias(libs.plugins.cyclonedx)
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
            username.set(nexusUsername)
            password.set(nexusPassword)
        }
    }
}