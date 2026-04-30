import org.jlleitschuh.gradle.ktlint.reporter.ReporterType
import org.jetbrains.kotlin.gradle.dsl.*
import com.android.build.api.dsl.*

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
    alias(libs.plugins.sonarqube)
    alias(libs.plugins.room) apply false
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

val jvmVersion = libs.versions.java.get().toInt()

plugins.withType<JavaPlugin> {
    extensions.configure<JavaPluginExtension> {
        toolchain { languageVersion.set(JavaLanguageVersion.of(jvmVersion)) }
    }
}

subprojects {
    // Kotlin JVM pure
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<KotlinJvmProjectExtension> {
            jvmToolchain(jvmVersion)
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(jvmVersion.toString()))
            }
        }
    }

    // Kotlin Android
    plugins.withId("org.jetbrains.kotlin.android") {
        extensions.configure<KotlinAndroidProjectExtension> {
            jvmToolchain(jvmVersion)
            compilerOptions {
                jvmTarget.set(JvmTarget.fromTarget(jvmVersion.toString()))
            }
        }
    }

    // Android (app / library)
    plugins.withId("com.android.application") {
        extensions.configure<ApplicationExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.toVersion(jvmVersion)
                targetCompatibility = JavaVersion.toVersion(jvmVersion)
            }
        }
    }
    plugins.withId("com.android.library") {
        extensions.configure<LibraryExtension> {
            compileOptions {
                sourceCompatibility = JavaVersion.toVersion(jvmVersion)
                targetCompatibility = JavaVersion.toVersion(jvmVersion)
            }
        }
    }
}

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

    configurations.named("ktlint") {
        resolutionStrategy {
            force("ch.qos.logback:logback-classic:1.3.14")
            force("ch.qos.logback:logback-core:1.3.14")
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

sonarqube {
    properties {
        val branch = System.getenv("GIT_BRANCH")
        val targetBranch = System.getenv("GIT_BRANCH_DEST")
        val pullRequestId = System.getenv("PULL_REQUEST")

        property("sonar.projectKey", "dhis2_dhis2-android-sdk")
        property("sonar.organization", "dhis2")
        property("sonar.host.url", "https://sonarcloud.io")
        property("sonar.projectName", "dhis2-android-sdk")
        property("sonar.java.binaries", "core/build/intermediates/javac/debug/classes")
        property(
            "sonar.coverage.jacoco.xmlReportPaths",
            "${rootProject.projectDir}/core/build/coverage-report/jacocoTestReport.xml",
        )

        if (pullRequestId.isNullOrEmpty()) {
            property("sonar.branch.name", branch)
        } else {
            property("sonar.pullrequest.base", targetBranch)
            property("sonar.pullrequest.branch", branch)
            property("sonar.pullrequest.key", pullRequestId)
        }
    }
}

// Ensure modules are built before SonarQube tries to resolve them
subprojects {
    tasks.matching { it.name == "sonarResolver" }.configureEach {
        dependsOn(":annotations:build")
    }
}

// Ensure core is compiled before running sonarqube analysis
tasks.named("sonarqube").configure {
    dependsOn(":core:assembleDebug")
}