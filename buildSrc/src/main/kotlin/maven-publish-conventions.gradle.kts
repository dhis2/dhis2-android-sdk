import java.net.URI

plugins {
    `maven-publish`
    signing
    id("org.jetbrains.dokka")
}

val VERSION_NAME: String by project

fun isReleaseBuild(): Boolean {
    return !VERSION_NAME.contains("SNAPSHOT")
}

val releaseRepositoryUrl: String = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"

val snapshotRepositoryUrl: String = "https://oss.sonatype.org/content/repositories/snapshots/"

fun getRepositoryUsername(): String? {
    return System.getenv("NEXUS_USERNAME")
}

fun getRepositoryPassword(): String? {
    return System.getenv("NEXUS_PASSWORD")
}

fun gpgKeyId(): String? {
    return System.getenv("GPG_KEY_ID")
}

fun gpgKeyLocation(): String? {
    return System.getenv("GPG_KEY_LOCATION")
}

fun gpgPassphrase(): String? {
    return System.getenv("GPG_PASSPHRASE")
}

gradle.taskGraph.whenReady(closureOf<TaskExecutionGraph> {
    if (gradle.taskGraph.allTasks.any { it is Sign }) {
        allprojects { ext["signing.keyId"] = gpgKeyId() }
        allprojects { ext["signing.secretKeyRingFile"] = gpgKeyLocation() }
        allprojects { ext["signing.password"] = gpgPassphrase() }
    }
})

tasks.dokkaJavadoc.configure {
    dependsOn("kaptReleaseKotlin")
    outputDirectory = layout.buildDirectory.file("dokkaJavadoc").get().asFile

    dokkaSourceSets {
        configureEach {
            perPackageOption {
                matchingRegex.set(".*.internal.*")
                suppress.set(true)
            }
        }
    }
}

val dokkaHtml = tasks.findByName("dokkaJavadoc")!!

val androidJavadocsJar = tasks.register("androidJavadocsJar", Jar::class) {
    archiveClassifier.set("javadoc")
    from(dokkaHtml.outputs)
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("maven") {
                from(components["release"])
                artifact(androidJavadocsJar)

                groupId = Props.GROUP
                artifactId = Props.POM_ARTIFACT_ID
                version = VERSION_NAME

                pom {
                    name = Props.POM_NAME
                    packaging = Props.POM_PACKAGING
                    description = Props.POM_DESCRIPTION
                    url = Props.POM_URL
                    licenses {
                        license {
                            name = Props.POM_LICENCE_NAME
                            url = Props.POM_LICENCE_URL
                            distribution = Props.POM_LICENCE_DIST
                        }
                    }
                    developers {
                        developer {
                            id = Props.POM_DEVELOPER_ID
                            name = Props.POM_DEVELOPER_NAME
                        }
                    }
                    scm {
                        connection = Props.POM_SCM_CONNECTION
                        developerConnection = Props.POM_SCM_DEV_CONNECTION
                        url = Props.POM_SCM_URL
                    }
                }
            }

            repositories {
                maven {
                    url = if (isReleaseBuild()) URI(releaseRepositoryUrl) else URI(snapshotRepositoryUrl)

                    credentials {
                        username = getRepositoryUsername()
                        password = getRepositoryPassword()
                    }
                }
            }
        }

        signing {
            setRequired({ isReleaseBuild() && gradle.taskGraph.hasTask("publishing") })
            sign(publishing.publications)
        }
    }
}
