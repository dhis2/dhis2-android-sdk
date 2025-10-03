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