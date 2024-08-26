/*
 * Copyright (c) 2016, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

plugins {
    id("com.android.library")
    id("com.google.devtools.ksp") version "${libs.versions.kotlin.get()}-1.0.16"
    id("kotlin-android")
    id("kotlin-kapt")
    id("maven-publish-conventions")
    id("jacoco-conventions")
    alias(libs.plugins.detekt)
}

apply(from = project.file("plugins/android-checkstyle.gradle"))
apply(from = project.file("plugins/android-pmd.gradle"))

repositories {
    mavenCentral()
    maven(url = "https://oss.sonatype.org/content/repositories/snapshots")
}

group = rootProject.group
version = rootProject.version

android {
    compileSdk = libs.versions.targetSdkVersion.get().toInt()

    defaultConfig {
        minSdk = libs.versions.minSdkVersion.get().toInt()
        testOptions.targetSdk = libs.versions.targetSdkVersion.get().toInt()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        multiDexEnabled = true
        vectorDrawables.useSupportLibrary = true

        buildConfigField("long", "VERSION_CODE", libs.versions.dhis2AndroidSdkCode.get())
        buildConfigField("String", "VERSION_NAME", "\"$version\"")
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true

        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    packaging {
        resources {
            excludes += listOf("META-INF/LICENSE", "META-INF/rxjava.properties")
        }
    }

    buildTypes {
        getByName("debug") {
            // a fix for the debugger not being able to find local scope variables
            enableAndroidTestCoverage = (project.hasProperty("coverage"))
        }
    }

    sourceSets {
        sourceSets.getByName("main") {
            java.srcDirs("build/generated/ksp/main/kotlin")
        }
        sourceSets.getByName("test") {
            resources.srcDirs("src/sharedTest/resources")
        }
        sourceSets.getByName("androidTest") {
            java.srcDirs("src/sharedTest/java")
            resources.srcDirs("src/sharedTest/resources")
        }
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
        }
    }

    lint {
        abortOnError = true
        disable += "MissingTranslation"
        warning += "InvalidPackage"
    }

    namespace = "org.hisp.dhis.android"
    buildFeatures {
        buildConfig = true
    }
}

dependencies {

    coreLibraryDesugaring(libs.desugaring)

    // RxJava
    api(libs.rx.java)
    api(libs.rx.android)

    // AndroidX
    api(libs.androidx.annotation)
    api(libs.androidx.paging.runtime)

    // Auto Value
    api(libs.google.auto.value.annotation)
    kapt(libs.google.auto.value)

    // Koin
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    ksp(libs.koin.compiler)

    // Jackson
    api(libs.jackson.core)
    api(libs.jackson.kotlin)

    // Square libraries
    api(libs.okhttp)
    api(libs.okhttp.mockwebserver)
    api(libs.retrofit.core)
    api(libs.retrofit.jackson)
    api(libs.retrofit.rxjava2)

    // Kotlin
    api(libs.kotlinx.datetime)
    api(libs.kotlinx.coroutines.core)
    api(libs.kotlinx.coroutines.rx2)

    // DHIS2 libraries
    api(libs.dhis2.compression)
    api(libs.dhis2.antlr.parser)

    // Extension which generates mappers for work with cursor and content values
    api(libs.auto.value.cursor.annotations)
    kapt(libs.auto.value.cursor)

    api(libs.sqlcipher)
    // From SQLCipher 4.5.5, it depends on androidx.sqlite:sqlite
    api(libs.sqlite)

    implementation(libs.zip4j)

    implementation(libs.openid.appauth)
    implementation(libs.listenablefuture.empty)

    // Java test dependencies
    testImplementation(libs.junit)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.google.truth) {
        exclude(group = "junit") // Android has JUnit built in.
    }

    testImplementation(libs.equalsverifier)
    testImplementation(libs.androidx.test.runner)
    testImplementation(libs.kotlinx.coroutines.test)

    // Android test dependencies
    androidTestImplementation(libs.mockito.core)
    androidTestImplementation(libs.livedata.testing.ktx)
    androidTestImplementation(libs.androidx.arch.core.testing)
    androidTestImplementation(libs.androidx.test.runner)
    androidTestImplementation(libs.androidx.test.rules)
    androidTestImplementation(libs.okhttp.logging)
    androidTestImplementation(libs.google.truth) {
        exclude(group = "junit") // Android has JUnit built in.
    }
    androidTestImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.paging.testing)

    debugImplementation(libs.facebook.soloader)
    debugImplementation(libs.facebook.flipper.core)
    debugImplementation(libs.facebook.flipper.network) {
        exclude(group = "com.squareup.okhttp3")
    }

    releaseImplementation(libs.facebook.flipper.noop)
}

detekt {
    toolVersion = libs.versions.detekt.get()
    config = files("config/detekt.yml")
    parallel = true
    buildUponDefaultConfig = false
}

tasks.dokkaJavadoc.configure {
    dependsOn("kaptReleaseKotlin")

    dokkaSourceSets {
        configureEach {
            perPackageOption {
                matchingRegex.set(".*.internal.*")
                suppress.set(true)
            }
        }
    }
}

tasks.dokkaHtml.configure {
    dependsOn("kaptReleaseKotlin")

    dokkaSourceSets {
        configureEach {
            perPackageOption {
                matchingRegex.set(".*.internal.*")
                suppress.set(true)
            }
        }
    }
}