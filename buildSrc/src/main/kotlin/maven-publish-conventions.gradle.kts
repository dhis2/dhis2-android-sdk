/*
 *  Copyright (c) 2004-2024, University of Oslo
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without
 *  modification, are permitted provided that the following conditions are met:
 *  Redistributions of source code must retain the above copyright notice, this
 *  list of conditions and the following disclaimer.
 *
 *  Redistributions in binary form must reproduce the above copyright notice,
 *  this list of conditions and the following disclaimer in the documentation
 *  and/or other materials provided with the distribution.
 *  Neither the name of the HISP project nor the names of its contributors may
 *  be used to endorse or promote products derived from this software without
 *  specific prior written permission.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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
    outputDirectory = file("${buildDir}/dokkaJavadoc")

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